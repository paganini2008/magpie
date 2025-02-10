package com.github.doodler.common.cloud;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import com.github.doodler.common.events.GlobalApplicationEventPublisher;
import com.github.doodler.common.events.OnlineGlobalApplicationEvent;

/**
 * 
 * @Description: AbstractDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
public abstract class AbstractDiscoveryClientRegistrar implements DiscoveryClientRegistrar {

    protected final DiscoveryClientProperties discoveryClientProperties;
    protected final ApplicationInfoHolder applicationInfoHolder;

    public AbstractDiscoveryClientRegistrar(DiscoveryClientProperties discoveryClientProperties,
            ApplicationInfoHolder applicationInfoHolder) {
        this.discoveryClientProperties = discoveryClientProperties;
        this.applicationInfoHolder = applicationInfoHolder;
    }

    protected ApplicationEventPublisher applicationEventPublisher;

    protected @Nullable GlobalApplicationEventPublisher globalApplicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void setGlobalApplicationEventPublisher(
            GlobalApplicationEventPublisher globalApplicationEventPublisher) {
        this.globalApplicationEventPublisher = globalApplicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (globalApplicationEventPublisher != null) {
            ApplicationInfo applicationInfo = applicationInfoHolder.get();
            OnlineGlobalApplicationEvent applicationEvent = new OnlineGlobalApplicationEvent(
                    applicationInfo.getServiceId(), applicationInfo);
            if (discoveryClientProperties.getOnlineGlobalEventDelay() > 0) {
                globalApplicationEventPublisher.publishEvent(applicationEvent,
                        discoveryClientProperties.getOnlineGlobalEventDelay(), TimeUnit.SECONDS);
            } else {
                globalApplicationEventPublisher.publishEvent(applicationEvent);
            }
        }
    }
}
