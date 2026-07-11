package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.common.redis.RedisDistributedLock;
import com.cpic.barsms.bpm.common.utils.DateFormatUtils;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateRequest;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;
import com.cpic.barsms.bpm.domain.service.BatchGenerateService;
import com.cpic.barsms.bpm.domain.service.BatchGenerateTxService;
import com.cpic.barsms.bpm.domain.service.BpmSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 批次生成编排服务（外层）。
 *
 * 职责：解析参数、解析场景、获取分布式锁，然后把真正的写库流程委托给
 * {@link BatchGenerateTxService#doGenerate}。
 *
 * 事务边界说明：本类不持有事务。事务在 {@link BatchGenerateTxService#doGenerate} 内部开启，
 * 该调用位于 {@link RedisDistributedLock#executeWithLock} 回调之内，因此
 * 「事务提交」先于「锁释放」发生，消除了原先锁释放在提交前导致的并发重复生成窗口。
 *
 * @Date 2026/7/11
 * @Created by xiaoliang.ruan
 */
@Slf4j
@Service
public class BatchGenerateServiceImpl implements BatchGenerateService {

    @Autowired
    private BpmSceneService bpmSceneService;
    @Autowired
    private BatchGenerateTxService batchGenerateTxService;
    @Autowired
    private RedisDistributedLock redisDistributedLock;

    /** 锁等待超时时间（毫秒） */
    private static final long LOCK_TIMEOUT = 30000;

    @Override
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

        // 持锁 → 事务（在锁内开启事务，提交后才释放锁）
        BatchGenerateResultDTO result = redisDistributedLock.executeWithLock(lockKey, LOCK_TIMEOUT, () ->
                batchGenerateTxService.doGenerate(tDay, tDayBase, sceneId, versionName, nodeCodePrefix));

        if (result == null) {
            throw new BizBatchException("获取分布式锁失败，请稍后重试");
        }

        return result;
    }
}
