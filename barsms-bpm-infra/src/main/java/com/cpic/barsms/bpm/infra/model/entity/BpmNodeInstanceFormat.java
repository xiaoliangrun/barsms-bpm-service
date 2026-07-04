package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 节点模板/格式配置
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_node_instance_format")
public class BpmNodeInstanceFormat extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("VERSION_NAME")
    private String versionName;

    @TableField("NODE_CODE")
    private String nodeCode;

    @TableField("TITLE")
    private String title;

    @TableField("STATUS")
    private String status;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("ORG_LEVEL")
    private String orgLevel;

    @TableField("DEPT_NAME")
    private String deptName;

    @TableField("OFFICE_NAME")
    private String officeName;

    @TableField("DELIVERY_INFO")
    private String deliveryInfo;

    @TableField("SQL5_TEXT")
    private String sql5Text;

    @TableField("SCENE_NAME")
    private String sceneName;

    @TableField("NODE_TYPE_CATEGORY")
    private String nodeTypeCategory;

    @TableField("NODE_TYPE_NAME")
    private String nodeTypeName;

    @TableField("DUE_DATE")
    private Date dueDate;

    @TableField("PLANNED_START")
    private Date plannedStart;

    @TableField("PLANNED_END")
    private Date plannedEnd;

    @TableField("DATA_ORG_LEVEL")
    private String dataOrgLevel;

    @TableField("CHANNEL")
    private String channel;

    @TableField("T_DAY")
    private Date tDay;
}
