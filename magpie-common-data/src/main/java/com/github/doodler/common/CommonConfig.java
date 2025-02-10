package com.github.doodler.common;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.github.doodler.common.context.BeanReflectionService;
import com.github.doodler.common.context.ContextPath;
import com.github.doodler.common.events.GlobalApplicationEventPublisher;
import com.github.doodler.common.events.GlobalApplicationEventPublisherBeanPostProcessor;

/**
 * 
 * @Description: CommonConfig
 * @Author: Fred Feng
 * @Date: 21/10/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class CommonConfig {

    @Bean
    public ContextPath contextPath() {
        return new ContextPath();
    }

    @ConditionalOnMissingBean
    @Bean
    public ExceptionTransformer exceptionTransferer() {
        return new DefaultExceptionTransformer();
    }

    @Bean
    public BeanReflectionService beanReflectionService() {
        return new BeanReflectionService();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnBean(GlobalApplicationEventPublisher.class)
    @Bean
    public GlobalApplicationEventPublisherBeanPostProcessor globalApplicationEventPublisherBeanPostProcessor() {
        return new GlobalApplicationEventPublisherBeanPostProcessor();
    }

}
