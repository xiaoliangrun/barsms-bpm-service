package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;
import com.cpic.barsms.bpm.domain.service.BatchGenerateTxService;
import com.cpic.barsms.bpm.domain.service.BpmBatchExecLogService;
import com.cpic.barsms.bpm.domain.service.BpmDeliverableService;
import com.cpic.barsms.bpm.domain.service.BpmNodeInstanceService;
import com.cpic.barsms.bpm.domain.service.DailyTaskGeneratorService;
import com.cpic.barsms.bpm.domain.service.MonthlyTaskGeneratorService;
import com.cpic.barsms.bpm.domain.service.OfficeMappingService;
import com.cpic.barsms.bpm.domain.service.ReferenceNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 批次生成的事务边界实现。{@link #doGenerate} 在一个事务内完成全部写库步骤，
 * 由外层 {@link BatchGenerateServiceImpl} 在持分布式锁后调用，保证「事务提交后才释放锁」。
 *
 * 失败日志的持久化由 {@link BpmBatchExecLogService} 的 REQUIRES_NEW 方法独立提交，
 * 不受本事务回滚影响。
 *
 * @Date 2026/7/11
 * @Created by xiaoliang.ruan
 */
@Slf4j
@Service
public class BatchGenerateTxServiceImpl implements BatchGenerateTxService {

    @Autowired
    private MonthlyTaskGeneratorService monthlyTaskGeneratorService;
    @Autowired
    private DailyTaskGeneratorService dailyTaskGeneratorService;
    @Autowired
    private OfficeMappingService officeMappingService;
    @Autowired
    private ReferenceNodeService referenceNodeService;
    @Autowired
    private BpmDeliverableService bpmDeliverableService;
    @Autowired
    private BpmNodeInstanceService bpmNodeInstanceService;
    @Autowired
    private BpmBatchExecLogService bpmBatchExecLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchGenerateResultDTO doGenerate(Date tDay, Date tDayBase, Long sceneId,
                                             String versionName, String nodeCodePrefix) {
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
            monthlyTaskGeneratorService.generate(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);

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
            deliverableCount = bpmDeliverableService.generate(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);

            bpmBatchExecLogService.markSuccess(logId, instanceCount, officeCount, deliverableCount);

        } catch (Exception e) {
            log.error("批量生成失败", e);
            // markFailed 走独立事务（REQUIRES_NEW），即使本事务回滚也能落库
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
