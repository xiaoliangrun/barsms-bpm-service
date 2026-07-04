package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 部门岗位映射
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_node_department")
public class BpmNodeDepartment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("INT_JOB_DEPT_CD")
    private String intJobDeptCd;

    @TableField("INT_JOB_DEPT_NAME")
    private String intJobDeptName;

    @TableField("ORG_LEVEL")
    private String orgLevel;

    @TableField("INT_JOB_BRANCH")
    private String intJobBranch;

    @TableField("INT_JOB_CEN_BRANCH")
    private String intJobCenBranch;

    @TableField("INT_JOB_BUSI_BRANCH")
    private String intJobBusiBranch;
}
