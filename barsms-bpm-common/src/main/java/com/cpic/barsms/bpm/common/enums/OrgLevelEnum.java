package com.cpic.barsms.bpm.common.enums;

/**
 * @Description 机构层级枚举
 * @Date 2026/6/30
 * @Created by xiaoliang.ruan
 */
public enum OrgLevelEnum {

    ROOT_COMPANY("总公司", "总"),
    BRANCH_COMPANY("分公司", "分"),
    CENTER_BRANCH_COMPANY("中支", "中"),
    SUB_BRANCH_COMPANY("支公司", "支");

    private final String fullName;
    private final String shortName;

    OrgLevelEnum(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    /**
     * 根据全称获取简称
     */
    public static String toShortName(String fullName) {
        if (fullName == null) {
            return null;
        }
        for (OrgLevelEnum level : values()) {
            if (level.fullName.equals(fullName)) {
                return level.shortName;
            }
        }
        return fullName;
    }

    /**
     * 根据简称获取全称
     */
    public static String toFullName(String shortName) {
        if (shortName == null) {
            return null;
        }
        for (OrgLevelEnum level : values()) {
            if (level.shortName.equals(shortName)) {
                return level.fullName;
            }
        }
        return shortName;
    }
}
