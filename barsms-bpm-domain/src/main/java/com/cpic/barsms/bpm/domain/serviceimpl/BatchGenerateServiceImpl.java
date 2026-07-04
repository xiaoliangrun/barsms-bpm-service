package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.common.redis.RedisDistributedLock;
import com.cpic.barsms.bpm.common.utils.DateFormatUtils;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateRequest;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;
import com.cpic.barsms.bpm.domain.service.BatchGenerateService;
import com.cpic.barsms.bpm.domain.service.BpmBatchExecLogService;
import com.cpic.barsms.bpm.domain.service.BpmNodeInstanceService;
import com.cpic.barsms.bpm.domain.service.BpmSceneService;
import com.cpic.barsms.bpm.domain.service.DailyTaskGeneratorService;
import com.cpic.barsms.bpm.domain.service.DeliverableService;
import com.cpic.barsms.bpm.domain.service.MonthlyTaskGeneratorSevice;
import com.cpic.barsms.bpm.domain.service.OfficeMappingService;
import com.cpic.barsms.bpm.domain.service.ReferenceNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class BatchGenerateServiceImpl implements BatchGenerateService {

    @Autowired
    private MonthlyTaskGeneratorSevice monthlyTaskGeneratorSevice;
    @Autowired
    private DailyTaskGeneratorService dailyTaskGeneratorService;
    @Autowired
    private OfficeMappingService officeMappingService;
    @Autowired
    private ReferenceNodeService referenceNodeService;
    @Autowired
    private DeliverableService deliverableService;
    @Autowired
    private BpmSceneService bpmSceneService;
    @Autowired
    private BpmNodeInstanceService bpmNodeInstanceService;
    @Autowired
    private BpmBatchExecLogService bpmBatchExecLogService;
    @Autowired
    private RedisDistributedLock redisDistributedLock;

    /** 锁等待超时时间（毫秒） */
    private static final long LOCK_TIMEOUT = 30000;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchGenerateResultDTO generate(BatchGenerateRequest batchGenerateRequest) {
        String versionName = batchGenerateRequest.getVersionName();
        String nodeCodePrefix = batchGenerateRequest.getNodeCode() != null ? batchGenerateRequest.getNodeCode() : "";

        // 根据 generate_batch2.sql：@t_day_format 是基准日期，t_day 是基准日期的下月第一天
        // tDay 参数是基准日期（format表的t_day），如果不传则默认下月第一天
        String tDayBaseStr = batchGenerateRequest.getTDay();
        if (tDayBaseStr == null || tDayBaseStr.isEmpty()) {
            tDayBaseStr = DateFormatUtils.getNextMonthFirstDay();
        }
        Date tDayBase = DateFormatUtils.parseDate(tDayBaseStr);
        // 实际生成的实例日期是基准日期的下月第一天
        Date tDay = DateFormatUtils.addOneMonth(tDayBase);
        String tDayStr = DateFormatUtils.formatDate(tDay);

        Long sceneId = bpmSceneService.resolveSceneId(versionName);
        String lockKey = versionName + "_" + tDayStr;

        log.info("开始批量生成, versionName={}, sceneId={}, tDayBase={}, tDay={}, nodeCodePrefix={}",
                versionName, sceneId, tDayBaseStr, tDayStr, nodeCodePrefix);

        // 使用 Redis 分布式锁
        BatchGenerateResultDTO batchGenerateResultDTO = redisDistributedLock.executeWithLock(lockKey, LOCK_TIMEOUT, () -> {
            return doGenerate(tDay, tDayBase, sceneId, versionName, nodeCodePrefix);
        });

        if (batchGenerateResultDTO == null) {
            throw new BizBatchException("获取分布式锁失败，请稍后重试");
        }

        return batchGenerateResultDTO;
    }

    /**
     * 执行批次生成逻辑
     */
    private BatchGenerateResultDTO doGenerate(Date tDay, Date tDayBase, Long sceneId, String versionName, String nodeCodePrefix) {
        // 幂等校验
        if (bpmBatchExecLogService.existsSuccess(tDay, versionName, nodeCodePrefix)) {
            throw new BizBatchException("当月同版本已生成过，请先删除历史数据");
        }

        Long logId = bpmBatchExecLogService.startLog(tDay, versionName, nodeCodePrefix);

        int instanceCount = 0;
        int officeCount = 0;
        int deliverableCount = 0;

        try {
            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.CLEAR_DATA);
            int deleted = bpmNodeInstanceService.deleteByTargetMonth(tDay, sceneId, nodeCodePrefix);
            log.info("Step1: 删除历史数据 {} 条", deleted);

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.INSERT_MONTHLY);
            monthlyTaskGeneratorSevice.generate(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.INSERT_DAILY);
            dailyTaskGeneratorService.generate(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);

            instanceCount = bpmNodeInstanceService.countByTargetMonth(tDay, sceneId, nodeCodePrefix);
            log.info("Step2+3: 节点实例合计 {} 条", instanceCount);

            if (instanceCount == 0) {
                throw new BizBatchException("未生成任何节点实例，请检查模板配置");
            }

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.GENERATE_OFFICE);
            officeCount = officeMappingService.generate(tDay, tDayBase, sceneId, nodeCodePrefix, versionName);

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.MARK_UNMATCHED);
            officeMappingService.markLogicDelete(tDay, sceneId, nodeCodePrefix);

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.SET_READONLY);
            referenceNodeService.updateReadOnlyStatus(tDay, sceneId, nodeCodePrefix);

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.SET_REFERENCE);
            referenceNodeService.updateReferenceNodes(tDay, sceneId, nodeCodePrefix);

            bpmBatchExecLogService.updateStep(logId, BatchStepEnum.GENERATE_DELIVERABLE);
            deliverableCount = deliverableService.generate(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);

            bpmBatchExecLogService.markSuccess(logId, instanceCount, officeCount, deliverableCount);

        } catch (Exception e) {
            log.error("批量生成失败", e);
            bpmBatchExecLogService.markFailed(logId, "", e.getMessage());
            throw e;
        }

        BatchGenerateResultDTO batchGenerateResultDTO = new BatchGenerateResultDTO();
        batchGenerateResultDTO.setInstanceCount(instanceCount);
        batchGenerateResultDTO.setOfficeCount(officeCount);
        batchGenerateResultDTO.setDeliverableCount(deliverableCount);
        return batchGenerateResultDTO;
    }
}
