package com.cpic.barsms.bpm.domain.service;

import com.cpic.barsms.bpm.common.enums.BatchStepEnum;

import java.util.Date;

public interface BpmBatchExecLogService {

    Long startLog(Date batchDate, String versionName, String nodeCode);

    void updateStep(Long logId, BatchStepEnum step);

    void markSuccess(Long logId, int instanceCount, int officeCount, int deliverableCount);

    void markFailed(Long logId, String errorStep, String errorMessage);

    boolean existsSuccess(Date batchDate, String versionName, String nodeCode);
}
