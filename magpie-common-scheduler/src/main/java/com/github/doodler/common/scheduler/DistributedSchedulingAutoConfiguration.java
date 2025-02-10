package com.github.doodler.common.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @Description: DistributedSchedulingAutoConfiguration
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class DistributedSchedulingAutoConfiguration {

    @ConditionalOnProperty(name = "spring.scheduler.run-mode", havingValue = "default",
            matchIfMissing = true)
    @Bean
    public DefaultCrosscuttingSpringScheduler defaultCrosscuttingSpringScheduler() {
        return new DefaultCrosscuttingSpringScheduler();
    }

    @ConditionalOnProperty(name = "spring.scheduler.run-mode", havingValue = "variable")
    @Bean
    public VariableCrosscuttingSpringScheduler variableCrosscuttingSpringScheduler() {
        return new VariableCrosscuttingSpringScheduler();
    }

}
