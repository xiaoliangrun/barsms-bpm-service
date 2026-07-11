package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpic.barsms.bpm.common.constants.BatchConstants;
import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import com.cpic.barsms.bpm.domain.service.BpmBatchExecLogService;
import com.cpic.barsms.bpm.infra.mapper.BpmBatchExecLogMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmBatchExecLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class BpmBatchExecLogServiceImpl extends ServiceImpl<BpmBatchExecLogMapper, BpmBatchExecLog> implements BpmBatchExecLogService {

    @Autowired
    private BpmBatchExecLogMapper bpmBatchExecLogMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long startLog(Date batchDate, String versionName, String nodeCode) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setBatchDate(batchDate);
        bpmBatchExecLog.setVersionName(versionName);
        bpmBatchExecLog.setNodeCode(nodeCode);
        bpmBatchExecLog.setStatus(BatchConstants.LOG_STATUS_RUNNING);
        bpmBatchExecLog.setCurrentStep(BatchStepEnum.CLEAR_DATA);
        bpmBatchExecLog.setStartTime(new Date());
        bpmBatchExecLog.setCreateTime(new Date());
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLog.setCreateBy(BatchConstants.CREATE_BY_SYSTEM);
        bpmBatchExecLog.setUpdateBy(BatchConstants.CREATE_BY_SYSTEM);
        bpmBatchExecLog.setDeleteFlag(BatchConstants.DELETE_FLAG_NORMAL);
        bpmBatchExecLogMapper.insert(bpmBatchExecLog);
        return bpmBatchExecLog.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStep(Long logId, BatchStepEnum step) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setId(logId);
        bpmBatchExecLog.setCurrentStep(step);
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLogMapper.updateById(bpmBatchExecLog);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(Long logId, int instanceCount, int officeCount, int deliverableCount) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setId(logId);
        bpmBatchExecLog.setStatus(BatchConstants.LOG_STATUS_SUCCESS);
        bpmBatchExecLog.setCurrentStep(BatchStepEnum.COMPLETED);
        bpmBatchExecLog.setInstanceCount(instanceCount);
        bpmBatchExecLog.setOfficeCount(officeCount);
        bpmBatchExecLog.setDeliverableCount(deliverableCount);
        bpmBatchExecLog.setEndTime(new Date());
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLogMapper.updateById(bpmBatchExecLog);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long logId, String errorStep, String errorMessage) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setId(logId);
        bpmBatchExecLog.setStatus(BatchConstants.LOG_STATUS_FAILED);
        bpmBatchExecLog.setErrorStep(errorStep);
        bpmBatchExecLog.setErrorMessage(errorMessage);
        bpmBatchExecLog.setEndTime(new Date());
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLogMapper.updateById(bpmBatchExecLog);
    }

    @Override
    public boolean existsSuccess(Date batchDate, String versionName, String nodeCode) {
        return bpmBatchExecLogMapper.existsSuccess(batchDate, versionName, nodeCode);
    }
}
