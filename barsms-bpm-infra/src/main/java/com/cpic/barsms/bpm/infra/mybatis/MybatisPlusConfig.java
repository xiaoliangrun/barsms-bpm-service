package com.cpic.barsms.bpm.infra.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.cpic.barsms.bpm.common.constants.BatchProperties;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@org.springframework.context.annotation.Configuration
@MapperScan("com.cpic.barsms.bpm.**.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 将 batch.properties 的值注入 MyBatis 全局变量，XML 中可用 ${batch.xxx} 引用
     */
    @Bean
    public MybatisPlusPropertiesCustomizer batchPropertiesCustomizer(BatchProperties batchProperties) {
        return properties -> {
            Properties variables = properties.getConfiguration().getVariables();
            if (variables == null) {
                variables = new Properties();
                properties.getConfiguration().setVariables(variables);
            }
            variables.setProperty("batch.headOfficeCode", batchProperties.getHeadOfficeCode());
            variables.setProperty("batch.headOfficeName", batchProperties.getHeadOfficeName());
            variables.setProperty("batch.defaultPriority", batchProperties.getDefaultPriority());
            variables.setProperty("batch.defaultChannel", batchProperties.getDefaultChannel());
            variables.setProperty("batch.defaultCustomFieldSchemeId", batchProperties.getDefaultCustomFieldSchemeId());
        };
    }
}
