package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("bpm_scene")
public class BpmScene extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("SCENE_KEY")
    private String sceneKey;

    @TableField("SCENE_NAME")
    private String sceneName;

    @TableField("SCENE_TYPE")
    private String sceneType;

    @TableField("STATUS")
    private String status;

    @TableField("T_DATE")
    private Date tDate;

    @TableField("START_DATE")
    private Date startDate;

    @TableField("TARGET_DATE")
    private Date targetDate;

    @TableField("OWNER_ORG_CODE")
    private String ownerOrgCode;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("DELETE_FLAG")
    private String deleteFlag;
}
