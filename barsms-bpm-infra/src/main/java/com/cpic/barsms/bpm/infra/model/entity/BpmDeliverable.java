package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 交付物
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("BPM_DELIVERABLE")
public class BpmDeliverable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("NODE_INSTANCE_ID")
    private Long nodeInstanceId;

    @TableField("DELIVERABLE_NAME")
    private String deliverableName;
}
