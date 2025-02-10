package com.github.doodler.common.events;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: GlobalApplicationEventPublisher
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
public interface GlobalApplicationEventPublisher {

    void subscribe(GlobalApplicationEventListener<? extends GlobalApplicationEvent> listener);

    void publishEvent(GlobalApplicationEvent event);

    void publishEvent(GlobalApplicationEvent event, long delay, TimeUnit timeUnit);

}
