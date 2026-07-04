package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 节点-机构岗位关联
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_node_instance_office")
public class BpmNodeInstanceOffice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("NODE_INSTANCE_ID")
    private Long nodeInstanceId;

    @TableField("OFFICE_CODE")
    private String officeCode;

    @TableField("OFFICE_NAME")
    private String officeName;

    @TableField("DEPT_CODE")
    private String deptCode;

    @TableField("DEPT_NAME")
    private String deptName;
}
