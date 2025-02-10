package com.github.doodler.common.cloud.redis;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import com.github.doodler.common.cloud.AbstractDiscoveryClientRegistrar;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoRegisteredEvent;
import com.github.doodler.common.cloud.DiscoveryClientProperties;

/**
 * 
 * @Description: RedisDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 11/08/2024
 * @Version 1.0.0
 */
public class RedisDiscoveryClientRegistrar extends AbstractDiscoveryClientRegistrar {

    public RedisDiscoveryClientRegistrar(DiscoveryClientProperties config,
            ApplicationInfoHolder applicationInfoHolder) {
        super(config, applicationInfoHolder);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        super.onApplicationEvent(event);
        applicationEventPublisher.publishEvent(
                new ApplicationInfoRegisteredEvent(this, applicationInfoHolder.get()));
    }



}
