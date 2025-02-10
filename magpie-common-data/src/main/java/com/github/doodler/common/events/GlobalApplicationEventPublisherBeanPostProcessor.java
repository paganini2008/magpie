package com.github.doodler.common.events;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 
 * @Description: GlobalApplicationEventPublisherBeanPostProcessor
 * @Author: Fred Feng
 * @Date: 15/01/2025
 * @Version 1.0.0
 */
public class GlobalApplicationEventPublisherBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private ObjectProvider<GlobalApplicationEventPublisher> globalApplicationEventPublisher;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof GlobalApplicationEventPublisherAware) {
            ((GlobalApplicationEventPublisherAware) bean).setGlobalApplicationEventPublisher(
                    globalApplicationEventPublisher.getObject());
        }
        return bean;
    }

}
