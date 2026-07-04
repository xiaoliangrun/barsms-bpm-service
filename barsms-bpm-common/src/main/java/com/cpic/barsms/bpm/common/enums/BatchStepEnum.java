package com.cpic.barsms.bpm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Description 批次执行步骤枚举
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
public enum BatchStepEnum {

    CLEAR_DATA("clear_data_1", "清除历史数据"),
    INSERT_MONTHLY("insert_monthly_2", "插入月度任务"),
    INSERT_DAILY("insert_daily_3", "插入每日任务"),
    GENERATE_OFFICE("generate_office_4", "生成岗位映射"),
    MARK_UNMATCHED("mark_unmatched_5", "标记未匹配记录"),
    SET_READONLY("set_readonly_6", "设置只读状态"),
    SET_REFERENCE("set_reference_7", "设置引用节点链"),
    GENERATE_DELIVERABLE("generate_deliverable_8", "生成交付物"),
    COMPLETED("completed_9", "执行完成");

    @EnumValue
    private final String code;
    private final String desc;

    BatchStepEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
