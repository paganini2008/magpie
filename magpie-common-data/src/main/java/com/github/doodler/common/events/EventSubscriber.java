package com.github.doodler.common.events;

import org.springframework.core.Ordered;

/**
 * 
 * @Description: EventSubscriber
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
public interface EventSubscriber<E> extends Ordered {

    void consume(E event, Context context);

    default void onError(E event, Throwable e, Context context) {}

    default void onComplete(E event, Throwable e, Context context) {}

    default int getOrder() {
        return 0;
    }

}
