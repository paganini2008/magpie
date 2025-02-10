package com.github.doodler.common.webmvc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 
 * @Description: ManagementServerPortConfigurator
 * @Author: Fred Feng
 * @Date: 30/01/2025
 * @Version 1.0.0
 */
public class ManagementServerPortConfigurator implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
            SpringApplication application) {
        if (!environment.containsProperty("management.server.port")
                || StringUtils.isBlank(environment.getProperty("management.server.port"))) {
            int serverPort = environment.getRequiredProperty("server.port", Integer.class);
            environment.getSystemProperties().put("management.server.port", serverPort + 10000);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
