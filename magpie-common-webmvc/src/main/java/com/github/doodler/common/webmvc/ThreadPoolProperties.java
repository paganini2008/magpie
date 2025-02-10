package com.github.doodler.common.webmvc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: ThreadPoolProperties
 * @Author: Fred Feng
 * @Date: 25/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolProperties {

    private TaskExecutor executor = new TaskExecutor();
    private TaskScheduler scheduler = new TaskScheduler();

    @Getter
    @Setter
    @ToString
    public static class TaskExecutor {

        private int poolSize = 8;
        private int maxPoolSize = 200;
        private int queueCapacity = 1000;
        private String threadNameFormat = "doodler-webapp-threads-%d";
        private String threadGroupName = "doodler-webapp-threads-group";
    }

    @Getter
    @Setter
    @ToString
    public static class TaskScheduler {

        private int poolSize = 20;
        private String threadNameFormat = "doodler-webapp-tasks-%d";
        private String threadGroupName = "doodler-webapp-tasks-group";
    }
}
