package com.github.doodler.common.scheduler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import com.github.doodler.common.cloud.PrimaryApplicationInfoReadyEvent;
import com.github.doodler.common.cloud.SecondaryApplicationInfoRefreshEvent;

/**
 * 
 * @Description: VariableCrosscuttingSpringScheduler
 * @Author: Fred Feng
 * @Date: 15/01/2025
 * @Version 1.0.0
 */
@Aspect
public class VariableCrosscuttingSpringScheduler implements CrosscuttingSpringScheduler {

    private AtomicBoolean primary;

    @Pointcut("execution(public * *(..))")
    public void signature() {}

    @Around("signature() && @annotation(scheduled)")
    public Object arround(ProceedingJoinPoint pjp, Scheduled scheduled) throws Throwable {
        if (((MethodSignature) pjp.getSignature()).getMethod().isAnnotationPresent(Default.class)) {
            return pjp.proceed();
        }
        if (primary == null) {
            return null;
        }
        Object targetBean = pjp.getTarget();
        List<Method> methodList;
        if (primary.get()) {
            methodList = MethodUtils.getMethodsListWithAnnotation(targetBean.getClass(),
                    RunAsPrimary.class);
        } else {
            methodList = MethodUtils.getMethodsListWithAnnotation(targetBean.getClass(),
                    RunAsSecondary.class);
        }
        for (Method method : methodList) {
            method.setAccessible(true);
            method.invoke(targetBean, pjp.getArgs());
        }
        return null;
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @EventListener({PrimaryApplicationInfoReadyEvent.class})
    public void onPrimaryApplicationInfoReadyEvent() {
        primary = new AtomicBoolean(true);
    }

    @EventListener({SecondaryApplicationInfoRefreshEvent.class})
    public void onSecondaryApplicationInfoRefreshEvent() {
        primary = new AtomicBoolean(false);
    }

}
