package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 节点实例
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_node_instance")
public class BpmNodeInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("T_DAY")
    private Date tDay;

    @TableField("SCENE_ID")
    private Long sceneId;

    @TableField("NODE_TYPE_ID")
    private Long nodeTypeId;

    @TableField("TITLE")
    private String title;

    @TableField("STATUS")
    private String status;

    @TableField("TIME_POINT")
    private Date timePoint;

    @TableField("PRIORITY")
    private String priority;

    @TableField("PROGRESS")
    private Integer progress;

    @TableField("DUE_DATE")
    private Date dueDate;

    @TableField("PLANNED_START")
    private Date plannedStart;

    @TableField("PLANNED_END")
    private Date plannedEnd;

    @TableField("CUSTOM_FIELD_SCHEME_ID")
    private Long customFieldSchemeId;

    @TableField("FIELD_SCHEME_DATA")
    private String fieldSchemeData;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("ORG_CODE")
    private String orgCode;

    @TableField("ORG_NAME")
    private String orgName;

    @TableField("ORG_BRANCH")
    private String orgBranch;

    @TableField("ORG_BRANCH_NAME")
    private String orgBranchName;

    @TableField("ORG_CEN_BRANCH")
    private String orgCenBranch;

    @TableField("ORG_CEN_BRANCH_NAME")
    private String orgCenBranchName;

    @TableField("ORG_BUSI_BRANCH")
    private String orgBusiBranch;

    @TableField("ORG_BUSI_BRANCH_NAME")
    private String orgBusiBranchName;

    @TableField("CHANNEL")
    private String channel;

    @TableField("NODE_CODE")
    private String nodeCode;

    @TableField("DATA_ORG_LEVEL")
    private String dataOrgLevel;

    @TableField("REFERENCE_NODE_ID")
    private Long referenceNodeId;
}
