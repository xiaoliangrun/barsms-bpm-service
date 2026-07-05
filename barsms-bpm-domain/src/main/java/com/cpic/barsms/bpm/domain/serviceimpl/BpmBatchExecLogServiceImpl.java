package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import com.cpic.barsms.bpm.domain.service.BpmBatchExecLogService;
import com.cpic.barsms.bpm.infra.mapper.BpmBatchExecLogMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmBatchExecLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class BpmBatchExecLogServiceImpl extends ServiceImpl<BpmBatchExecLogMapper, BpmBatchExecLog> implements BpmBatchExecLogService {

    @Autowired
    private BpmBatchExecLogMapper bpmBatchExecLogMapper;

    @Override
    public Long startLog(Date batchDate, String versionName, String nodeCode) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setBatchDate(batchDate);
        bpmBatchExecLog.setVersionName(versionName);
        bpmBatchExecLog.setNodeCode(nodeCode);
        bpmBatchExecLog.setStatus("RUNNING");
        bpmBatchExecLog.setCurrentStep(BatchStepEnum.CLEAR_DATA);
        bpmBatchExecLog.setStartTime(new Date());
        bpmBatchExecLog.setCreateTime(new Date());
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLog.setCreateBy("SYSTEM");
        bpmBatchExecLog.setUpdateBy("SYSTEM");
        bpmBatchExecLog.setDeleteFlag("0");
        bpmBatchExecLogMapper.insert(bpmBatchExecLog);
        return bpmBatchExecLog.getId();
    }

    @Override
    public void updateStep(Long logId, BatchStepEnum step) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setId(logId);
        bpmBatchExecLog.setCurrentStep(step);
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLogMapper.updateById(bpmBatchExecLog);
    }

    @Override
    public void markSuccess(Long logId, int instanceCount, int officeCount, int deliverableCount) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setId(logId);
        bpmBatchExecLog.setStatus("SUCCESS");
        bpmBatchExecLog.setCurrentStep(BatchStepEnum.COMPLETED);
        bpmBatchExecLog.setInstanceCount(instanceCount);
        bpmBatchExecLog.setOfficeCount(officeCount);
        bpmBatchExecLog.setDeliverableCount(deliverableCount);
        bpmBatchExecLog.setEndTime(new Date());
        bpmBatchExecLog.setUpdateTime(new Date());
        bpmBatchExecLogMapper.updateById(bpmBatchExecLog);
    }

    @Override
    public void markFailed(Long logId, String errorStep, String errorMessage) {
        BpmBatchExecLog bpmBatchExecLog = new BpmBatchExecLog();
        bpmBatchExecLog.setId(logId);
        bpmBatchExecLog.setStatus("FAILED");
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
