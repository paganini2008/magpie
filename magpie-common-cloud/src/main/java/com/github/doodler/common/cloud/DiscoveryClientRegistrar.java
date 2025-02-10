package com.github.doodler.common.cloud;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import com.github.doodler.common.events.GlobalApplicationEventPublisherAware;

/**
 * 
 * @Description: DiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 11/08/2024
 * @Version 1.0.0
 */
public interface DiscoveryClientRegistrar extends ApplicationEventPublisherAware,
        GlobalApplicationEventPublisherAware, ApplicationListener<ApplicationReadyEvent> {

}
