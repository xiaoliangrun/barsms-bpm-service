package com.cpic.barsms.bpm.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import com.cpic.barsms.bpm.infra.model.entity.BpmBatchExecLog;

import java.util.Date;

public interface BpmBatchExecLogService extends IService<BpmBatchExecLog> {

    Long startLog(Date batchDate, String versionName, String nodeCode);

    void updateStep(Long logId, BatchStepEnum step);

    void markSuccess(Long logId, int instanceCount, int officeCount, int deliverableCount);

    void markFailed(Long logId, String errorStep, String errorMessage);

    boolean existsSuccess(Date batchDate, String versionName, String nodeCode);
}
