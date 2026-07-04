package com.cpic.barsms.bpm.common.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 批次生成配置属性（从 batch.properties 读取）
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Data
@Component
@ConfigurationProperties(prefix = "batch")
public class BatchProperties {

    /** 总公司机构代码 */
    private String headOfficeCode = "00000000000000";

    /** 总公司名称 */
    private String headOfficeName = "集团总公司";

    /** 默认优先级 */
    private String defaultPriority = "中";

    /** 默认渠道 */
    private String defaultChannel = "PAA";

    /** 默认自定义字段方案ID */
    private String defaultCustomFieldSchemeId = "1";

    /** 中支需要排除的机构代码列表（逗号分隔） */
    private List<String> excludedOrgCodes = new ArrayList<>();
}
