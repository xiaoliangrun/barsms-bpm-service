package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("CREATED_AT")
    private Date createTime;

    @TableField("UPDATED_AT")
    private Date updateTime;

    @TableField("CREATED_BY")
    private String createBy;

    @TableField("UPDATED_BY")
    private String updateBy;

    @TableField("DELETE_FLAG")
    private String deleteFlag;
}
