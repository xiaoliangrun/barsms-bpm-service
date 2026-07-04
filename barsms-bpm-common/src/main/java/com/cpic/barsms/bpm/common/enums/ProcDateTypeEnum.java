package com.cpic.barsms.bpm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Description 任务周期类型枚举
 * @Date 2026/6/30
 * @Created by xiaoliang.ruan
 */
public enum ProcDateTypeEnum {

    MONTHLY("每月", "月度任务"),
    DAILY("每日", "每日任务");

    @EnumValue
    private final String code;
    private final String description;

    ProcDateTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
