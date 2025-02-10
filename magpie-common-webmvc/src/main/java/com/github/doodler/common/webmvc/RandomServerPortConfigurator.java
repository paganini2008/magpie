package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.SERVER_PORT_FROM;
import static com.github.doodler.common.Constants.SERVER_PORT_TO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import com.github.doodler.common.utils.NetUtils;

/**
 * @Description: RandomServerPortConfigurator
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
public class RandomServerPortConfigurator implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
            SpringApplication application) {
        if (environment.containsProperty("randomServerPort")
                && (!environment.containsProperty("server.port")
                        || StringUtils.isBlank(environment.getProperty("server.port")))) {
            int randomPort = NetUtils.getRandomPort(SERVER_PORT_FROM, SERVER_PORT_TO);
            environment.getSystemProperties().put("server.port", randomPort);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }
}
