package com.github.doodler.common.quartz.executor;

import static com.github.doodler.common.quartz.JobConstants.JOB_EXECUTOR_HTTP_HEADERS;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.DiscoveryClientService;
import com.github.doodler.common.cloud.lb.LbRestTemplate;
import com.github.doodler.common.quartz.QuartzProperties;
import com.github.doodler.common.quartz.scheduler.JobOperations;
import com.github.doodler.common.retry.RetryOperations;

/**
 * @Description: QuartzExecutorConfiguration
 * @Author: Fred Feng
 * @Date: 21/06/2023
 * @Version 1.0.0
 */
@Import({JobStartController.class})
@EnableConfigurationProperties({QuartzProperties.class})
@Configuration(proxyBeanMethods = false)
public class QuartzExecutorConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private QuartzProperties quartzProperties;

    @ConditionalOnMissingBean(name = JOB_EXECUTOR_HTTP_HEADERS)
    @Bean(JOB_EXECUTOR_HTTP_HEADERS)
    public HttpHeaders httpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    @Bean
    public JobOperations jobOperations(LbRestTemplate restTemplate,
            @Qualifier(JOB_EXECUTOR_HTTP_HEADERS) HttpHeaders httpHeaders) {
        return new RestJobOperations(restTemplate, httpHeaders);
    }

    @Bean
    public JobExecutionBeanProcessor jobExecutionBeanProcessor(JobOperations jobOperations,
            JobExecutionIgniter jobIgniter, RetryOperations retryOperations,
            @Lazy ThreadPoolTaskExecutor taskExecutor, Marker marker) {
        return new JobExecutionBeanProcessor(quartzProperties, jobOperations, jobIgniter,
                retryOperations, taskExecutor, marker);
    }

    @ConditionalOnMissingBean
    @Bean
    public JobBeanFactory jobBeanFactory() {
        return new DefaultJobBeanFactory();
    }

    @Bean
    public JobService jobService(JobBeanFactory jobBeanFactory,
            ApplicationInfoHolder applicationInfoHolder, LbRestTemplate restTemplate) {
        return new JobService(jobBeanFactory, applicationInfoHolder, restTemplate);
    }

    @Bean
    public SimpleJobTemplate simpleJobTemplate(JobOperations jobOperations, Marker marker) {
        SimpleJobTemplate jobTemplate = new SimpleJobTemplate(jobOperations, marker);
        jobTemplate.setDefaultJobGroupName(applicationName.toUpperCase());
        jobTemplate.setDefaultTriggerGroupName(
                String.format("%s-TRIGGER-GROUP", applicationName.toUpperCase()));
        return jobTemplate;
    }

    @Bean
    public JobExecutionIgniter jobIgniter() {
        return new JobExecutionIgniter();
    }

    @ConditionalOnProperty(name = "management.health.quartz.enabled", havingValue = "true",
            matchIfMissing = true)
    @Bean
    public QuartzExecutorHealthIndicator quartzHealthIndicator(
            DiscoveryClientService discoveryClientService, LbRestTemplate restTemplate,
            @Qualifier(JOB_EXECUTOR_HTTP_HEADERS) HttpHeaders httpHeaders) {
        return new QuartzExecutorHealthIndicator(discoveryClientService, restTemplate, httpHeaders);
    }
}
