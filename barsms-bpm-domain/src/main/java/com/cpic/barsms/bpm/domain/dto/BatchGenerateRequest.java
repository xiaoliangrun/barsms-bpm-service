package com.cpic.barsms.bpm.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description 批处理请求
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
public class BatchGenerateRequest {

    /** 版本名称 */
    @NotBlank(message = "版本名称不能为空")
    private String versionName;

    /** 节点编码前缀（空则全部） */
    private String nodeCode;

    /** 基准日期（格式：yyyy-MM-dd，如：2026-06-01），空则默认下月第一天 */
    private String tDay;

}
