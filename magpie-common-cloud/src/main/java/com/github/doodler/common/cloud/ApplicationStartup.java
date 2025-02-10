package com.github.doodler.common.cloud;

import java.util.Map;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import com.github.doodler.common.Constants;
import com.github.doodler.common.utils.Env;
import lombok.Setter;

/**
 * 
 * @Description: ApplicationStartup
 * @Author: Fred Feng
 * @Date: 12/01/2025
 * @Version 1.0.0
 */
public class ApplicationStartup implements MetadataCollector, EnvironmentAware {

    @Setter
    private Environment environment;

    @Override
    public Map<String, String> getInitialData() {
        return Map.of("uptime", String.valueOf(System.currentTimeMillis()), "pid",
                String.valueOf(Env.getPid()), "executablePath", System.getProperty("user.dir"),
                "executableFile",
                String.format("%s-%s.jar",
                        environment.getRequiredProperty("spring.application.name"),
                        Constants.VERSION),
                "vmArgs", Env.getVmArgs(), "user", System.getProperty("user.name"));
    }

}
