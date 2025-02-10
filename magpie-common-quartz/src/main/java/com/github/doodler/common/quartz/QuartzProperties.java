package com.github.doodler.common.quartz;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: QuartzProperties
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("doodler.quartz")
public class QuartzProperties {

    private Scheduler scheduler = new Scheduler();
    private Executor executor = new Executor();

    @Getter
    @Setter
    public static class Scheduler {

    }

    @Getter
    @Setter
    public static class Executor {
        private Http http = new Http();
    }

    @Getter
    @Setter
    public static class Http {
        private int maxRetryCount = 10;
        private long retryInterval = 10000;
    }

}
