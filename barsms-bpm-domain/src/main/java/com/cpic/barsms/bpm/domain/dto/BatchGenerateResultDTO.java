package com.cpic.barsms.bpm.domain.dto;

import lombok.Data;

/**
 * @Description 批处理结果
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
public class BatchGenerateResultDTO {
    /** 生成节点实例数 */
    private int instanceCount;
    /** 生成岗位关联数 */
    private int officeCount;
    /** 生成交付物数 */
    private int deliverableCount;
}
