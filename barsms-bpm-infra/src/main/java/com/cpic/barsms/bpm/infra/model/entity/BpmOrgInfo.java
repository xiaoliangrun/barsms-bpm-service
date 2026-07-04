package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 机构码表 BPM_ORG_INFO
 */
@Data
@TableName("BPM_ORG_INFO")
public class BpmOrgInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("ORG_CODE")
    private String orgCode;

    @TableField("ORG_NAME")
    private String orgName;

    @TableField("ORG_SNAME")
    private String orgSname;

    @TableField("ORG_TYPE")
    private String orgType;

    @TableField("ORG_LEVEL")
    private String orgLevel;

    @TableField("ORG_STATUS")
    private String orgStatus;

    @TableField("SUPER_ORG_CODE")
    private String superOrgCode;

    @TableField("HIERARCHY_CODE")
    private String hierarchyCode;

    @TableField("REGION_CODE")
    private String regionCode;

    @TableField("REGION_NAME")
    private String regionName;

    @TableField("CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField("CREATED_BY")
    private String createBy;

    @TableField("UPDATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField("UPDATED_BY")
    private String updateBy;

    @TableField("IS_DELETED")
    private String deleteFlag;
}
