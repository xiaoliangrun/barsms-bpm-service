package com.cpic.barsms.bpm.infra.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 日历维度
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@TableName("bpm_dim_calendar")
public class BpmDimCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("CAL_DATE")
    private String calDate;

    @TableField("MONTH")
    private String month;

    @TableField("DAY")
    private Integer day;
}
