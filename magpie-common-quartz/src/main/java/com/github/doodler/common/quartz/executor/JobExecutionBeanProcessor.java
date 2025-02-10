package com.github.doodler.common.quartz.executor;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Marker;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClientException;
import com.github.doodler.common.quartz.QuartzProperties;
import com.github.doodler.common.quartz.annotation.Job;
import com.github.doodler.common.quartz.annotation.Trigger;
import com.github.doodler.common.quartz.scheduler.JobOperations;
import com.github.doodler.common.retry.RetryOperations;
import com.github.doodler.common.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JobExecutionBeanProcessor
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
@RequiredArgsConstructor
public class JobExecutionBeanProcessor implements BeanPostProcessor {

    @Value("${spring.application.name}")
    private String applicationName;

    private final QuartzProperties quartzProperties;
    private final JobOperations jobOperations;
    private final JobExecutionIgniter jobIgniter;
    private final RetryOperations retryOperations;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final Marker marker;



    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> annotatedMethods =
                MethodUtils.getMethodsListWithAnnotation(targetClass, Job.class);
        if (CollectionUtils.isNotEmpty(annotatedMethods)) {
            for (Method method : annotatedMethods) {
                jobIgniter.runOnApplicationReady(() -> {
                    declareJob(method);
                });
            }
        }
        return bean;
    }

    private void declareJob(Method method) {
        final Job job = method.getAnnotation(Job.class);
        String jobName = job.name();
        String jobGroup =
                StringUtils.isNotBlank(job.group()) ? job.group() : applicationName.toUpperCase();
        JobDefination.JobDefinationBuilder builder = JobDefination.builder();
        builder.applicationName(applicationName).jobName(jobName).jobGroup(jobGroup)
                .className(method.getDeclaringClass().getName()).method(method.getName())
                .description(job.description()).maxRetryCount(job.maxRetryCount());
        if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(String.class)) {
            builder.initialParameter("<NONE>");
        }
        Trigger trigger = job.trigger();
        String triggerName = trigger.name();
        if (StringUtils.isBlank(triggerName)) {
            triggerName = String.format("%s-TRIGGER", jobName.toUpperCase());
        }
        String triggerGroup = trigger.group();
        if (StringUtils.isBlank(triggerGroup)) {
            triggerGroup = String.format("%s-TRIGGER-GROUP", applicationName.toUpperCase());
        }

        TriggerDefination.TriggerDefinationBuilder triggerBuilder = TriggerDefination.builder();
        triggerBuilder.triggerName(triggerName);
        triggerBuilder.triggerGroup(triggerGroup);
        triggerBuilder.cron(trigger.cron());

        if (trigger.period() > 0) {
            triggerBuilder.period(TimeUnit.SECONDS.convert(trigger.period(), trigger.timeUnit()));
        }
        if (trigger.initialDelay() > 0) {
            triggerBuilder.startTime(new Date(System.currentTimeMillis()
                    + TimeUnit.MILLISECONDS.convert(trigger.initialDelay(), trigger.timeUnit())));
        }
        triggerBuilder.repeatCount(trigger.repeatCount());
        triggerBuilder.description(trigger.description());

        JobDefination jobDefination = builder.build();
        TriggerDefination triggerDefination = triggerBuilder.build();
        jobDefination.setTriggerDefination(triggerDefination);
        try {
            doPersistJob(jobDefination, trigger.update());
        } catch (Exception e) {
            if (e instanceof RestClientException) {
                taskExecutor.submit(() -> {
                    syncJobDefinationWithRetryPolicy(jobDefination, trigger.update());
                });
            } else {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private void syncJobDefinationWithRetryPolicy(JobDefination jobDefination, boolean updated) {
        try {
            QuartzProperties.Http http = quartzProperties.getExecutor().getHttp();
            retryOperations.execute(() -> {
                doPersistJob(jobDefination, updated);
                return jobDefination;
            }, http.getMaxRetryCount(), http.getRetryInterval(),
                    new Class[] {RestClientException.class}, null, new RetryListener() {

                        @Override
                        public <T, E extends Throwable> boolean open(RetryContext context,
                                RetryCallback<T, E> callback) {
                            if (log.isInfoEnabled()) {
                                log.info("Start retrying to sync JobDetail:{} , updated: {}",
                                        JacksonUtils.toJsonString(jobDefination), updated);
                            }
                            return true;
                        }

                        @Override
                        public <T, E extends Throwable> void close(RetryContext context,
                                RetryCallback<T, E> callback, Throwable e) {
                            if (e != null) {
                                if (log.isWarnEnabled()) {
                                    log.warn(marker,
                                            "[Retried {}] Failed to sync JobDetail:{}, updated: {}, reason: {}",
                                            context.getRetryCount(),
                                            JacksonUtils.toJsonString(jobDefination), updated,
                                            e.getMessage());
                                    if (log.isWarnEnabled()) {
                                        log.warn(
                                                "Pending for running job after job service being available ...");
                                    }
                                    jobIgniter.runOnApplicationRecovery(
                                            () -> syncJobDefinationWithRetryPolicy(jobDefination,
                                                    updated));
                                }
                            }
                        }

                        @Override
                        public <T, E extends Throwable> void onError(RetryContext context,
                                RetryCallback<T, E> callback, Throwable e) {
                            if (log.isWarnEnabled()) {
                                log.warn("[Retrying {}] Failed to sync JobDetail by reason:{}",
                                        context.getRetryCount(), e.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Finally being unable to sync JobDetail:{}", e.getMessage());
            }
        }
    }

    private void doPersistJob(JobDefination jobDefination, boolean updated) throws Exception {
        if (jobOperations.isJobExists(jobDefination.getJobName(), jobDefination.getJobGroup())) {
            if (updated) {
                Date firstFiredDate;
                if (StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
                    firstFiredDate = jobOperations.modifyCronJob(jobDefination);
                } else {
                    firstFiredDate = jobOperations.modifyJob(jobDefination);
                }
                if (log.isInfoEnabled()) {
                    log.info(marker, "Job {}.{} is updated and first fired date will at {}",
                            jobDefination.getJobGroup(), jobDefination.getJobName(),
                            (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
                }
            }
        } else {
            Date firstFiredDate;
            if (StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
                firstFiredDate = jobOperations.addCronJob(jobDefination);
            } else {
                firstFiredDate = jobOperations.addJob(jobDefination);
            }
            if (log.isInfoEnabled()) {
                log.info(marker, "Job {}.{} is added and first fired date will at {}",
                        jobDefination.getJobGroup(), jobDefination.getJobName(),
                        (firstFiredDate != null ? firstFiredDate.toString() : "<NONE>"));
            }
        }
    }

}
