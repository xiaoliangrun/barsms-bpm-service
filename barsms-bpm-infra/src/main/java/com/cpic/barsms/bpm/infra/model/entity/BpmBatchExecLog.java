package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 批次执行日志
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_batch_exec_log")
public class BpmBatchExecLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("BATCH_DATE")
    private Date batchDate;

    @TableField("VERSION_NAME")
    private String versionName;

    @TableField("NODE_CODE")
    private String nodeCode;

    @TableField("STATUS")
    private String status;

    @TableField("CURRENT_STEP")
    private BatchStepEnum currentStep;

    @TableField("ERROR_STEP")
    private String errorStep;

    @TableField("ERROR_MESSAGE")
    private String errorMessage;

    @TableField("INSTANCE_COUNT")
    private Integer instanceCount;

    @TableField("OFFICE_COUNT")
    private Integer officeCount;

    @TableField("DELIVERABLE_COUNT")
    private Integer deliverableCount;

    @TableField("START_TIME")
    private Date startTime;

    @TableField("END_TIME")
    private Date endTime;
}
