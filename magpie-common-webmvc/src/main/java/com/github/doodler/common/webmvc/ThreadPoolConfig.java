package com.github.doodler.common.webmvc;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;
import com.github.doodler.common.context.AsyncErrorHandler;
import com.github.doodler.common.context.ConditionalOnNotApplication;
import com.github.doodler.common.webmvc.actuator.ThreadPoolMetricsCollector;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @Description: ThreadPoolConfig
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class ThreadPoolConfig {

    @Value("${spring.application.name}")
    private String applicatonName;

    // @Bean
    public RequestContextTaskDecorator requestContextTaskDecorator() {
        return new RequestContextTaskDecorator();
    }

    @Bean
    public TaskExecutorCustomizer taskExecutorCustomizer(TaskExecutionProperties properties,
            ErrorHandler errorHandler) {
        return executor -> {
            TaskExecutionProperties.Pool pool = properties.getPool();
            executor.setThreadNamePrefix(String.format("%s-task-executor-", applicatonName));
            executor.setCorePoolSize(Math.max(pool.getCoreSize(), 100));
            executor.setMaxPoolSize(Math.min(pool.getMaxSize(), 200));
            executor.setQueueCapacity(Math.min(pool.getQueueCapacity(), 2000));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.setKeepAliveSeconds(60);
            executor.setAllowCoreThreadTimeOut(true);
            executor.setWaitForTasksToCompleteOnShutdown(true);
            executor.setAwaitTerminationSeconds(60);
        };
    }

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer(TaskSchedulingProperties properties,
            ErrorHandler errorHandler) {
        return scheduler -> {
            scheduler.setThreadNamePrefix(String.format("%s-task-scheduler-", applicatonName));
            scheduler.setPoolSize(Math.max(properties.getPool().getSize(), 10));
            scheduler.setAwaitTerminationSeconds(60);
            scheduler.setRemoveOnCancelPolicy(true);
            scheduler.setWaitForTasksToCompleteOnShutdown(true);
            scheduler.setErrorHandler(errorHandler);
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorHandler defaultErrorHandler() {
        return new AsyncErrorHandler();
    }

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(
            ConfigurableListableBeanFactory beanFactory, TaskExecutor taskExecutor,
            ErrorHandler errorHandler) {
        SimpleApplicationEventMulticaster multicaster =
                new SimpleApplicationEventMulticaster(beanFactory);
        multicaster.setTaskExecutor(taskExecutor);
        multicaster.setErrorHandler(errorHandler);
        return multicaster;
    }

    @ConditionalOnNotApplication(value = {"doodler-job-service"})
    @Bean
    public ThreadPoolMetricsCollector threadPoolMetricsCollector(
            ThreadPoolTaskExecutor taskExecutor,
            @Autowired(required = false) ThreadPoolTaskScheduler taskScheduler,
            MeterRegistry registry) {
        return new ThreadPoolMetricsCollector(taskExecutor, taskScheduler, registry);
    }
}
