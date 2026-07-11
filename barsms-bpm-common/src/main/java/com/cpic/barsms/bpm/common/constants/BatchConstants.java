package com.cpic.barsms.bpm.common.constants;

/**
 * 批次生成相关常量。集中管理原本散落在业务代码中的魔法值。
 *
 * @Date 2026/7/11
 * @Created by xiaoliang.ruan
 */
public final class BatchConstants {

    private BatchConstants() {}

    /** 机构维度未细分时的占位编码 */
    public static final String ORG_ALL = "ALL";

    /** 默认系统操作人 */
    public static final String CREATE_BY_SYSTEM = "SYSTEM";

    /** 默认管理员操作人（岗位映射等无明确创建人的场景） */
    public static final String CREATE_BY_ADM = "ADM";

    /** 默认自定义字段方案ID */
    public static final long DEFAULT_CUSTOM_FIELD_SCHEME_ID = 1L;

    /** 默认进度（新建节点实例初始进度） */
    public static final int DEFAULT_PROGRESS = 0;

    /** 逻辑删除标志：未删除 */
    public static final String DELETE_FLAG_NORMAL = "0";

    // ---- 批次执行日志状态 ----

    public static final String LOG_STATUS_RUNNING = "RUNNING";
    public static final String LOG_STATUS_SUCCESS = "SUCCESS";
    public static final String LOG_STATUS_FAILED = "FAILED";

}
