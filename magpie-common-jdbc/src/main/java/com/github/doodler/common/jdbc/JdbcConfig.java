package com.github.doodler.common.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @Description: JdbcConfig
 * @Author: Fred Feng
 * @Date: 14/01/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class JdbcConfig {

    @Bean
    public TypeHandler enumTypeHandler() {
        return new EnumTypeHandler();
    }

}
