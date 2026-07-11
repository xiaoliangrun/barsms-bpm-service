package com.cpic.barsms.bpm.domain.service;

import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;

import java.util.Date;

/**
 * 批次生成的事务边界服务。
 *
 * 设计要点：事务必须在分布式锁内部开启、在释放锁之前提交，
 * 否则会出现「锁已释放但数据尚未提交」的窗口期，导致并发请求通过幂等校验后重复生成。
 * 因此把真正写库的 {@link #doGenerate} 单独放在一个带 {@code @Transactional} 的 Bean 中，
 * 由外层 {@link BatchGenerateService} 在持锁后调用。
 *
 * @Date 2026/7/11
 * @Created by xiaoliang.ruan
 */
public interface BatchGenerateTxService {

    /**
     * 在事务内执行批次生成的全部写库步骤。
     *
     * @param tDay           实际生成月份的第一天
     * @param tDayBase       模板查询基准日期
     * @param sceneId        场景ID
     * @param versionName    版本名称
     * @param nodeCodePrefix 节点编码前缀（可为空）
     * @return 生成结果统计
     */
    BatchGenerateResultDTO doGenerate(Date tDay, Date tDayBase, Long sceneId,
                                      String versionName, String nodeCodePrefix);
}
