package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 节点类型
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_node_type")
public class BpmNodeType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("CATEGORY")
    private String category;

    @TableField("TYPE_NAME")
    private String typeName;

    @TableField("TYPE_KEY")
    private String typeKey;
}
