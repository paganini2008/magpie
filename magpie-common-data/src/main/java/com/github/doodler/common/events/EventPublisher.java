package com.github.doodler.common.events;

import java.util.Collection;

/**
 * 
 * @Description: EventPublisher
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
public interface EventPublisher<T> {

    void enableBufferCleaner(boolean enabled);

    void setContext(Context context);

    int subscribe(Collection<EventSubscriber<T>> subscribers);

    void publish(T event);

    long getMaxBufferCapacity();

    long getEstimatedLagAmount();

    long remainingBufferSize();

    default boolean isActive() {
        return getEstimatedLagAmount() > 0 || remainingBufferSize() > 0;
    }

    default void destroy() {}

}
