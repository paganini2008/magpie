package com.github.doodler.common.redis.pubsub;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import com.github.doodler.common.events.GlobalApplicationEvent;
import com.github.doodler.common.events.GlobalApplicationEventListener;
import com.github.doodler.common.events.GlobalApplicationEventPublisher;
import com.github.doodler.common.utils.ExceptionUtils;
import com.github.doodler.common.utils.MapUtils;
import io.lettuce.core.dynamic.support.ResolvableType;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: GlobalApplicationEventPublisher
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
@Slf4j
public class PubSubGlobalApplicationEventPublisher implements GlobalApplicationEventPublisher {

    private final RedisPubSubService redisPubSubService;

    public PubSubGlobalApplicationEventPublisher(RedisPubSubService redisPubSubService,
            @Nullable List<GlobalApplicationEventListener<? extends GlobalApplicationEvent>> listeners) {
        this.redisPubSubService = redisPubSubService;
        if (listeners != null) {
            listeners.forEach(l -> subscribe(l));
        }
    }

    private Map<Type, List<GlobalApplicationEventListener<? extends GlobalApplicationEvent>>> globalApplicationEventListeners =
            new ConcurrentHashMap<>();

    @Override
    public void subscribe(
            GlobalApplicationEventListener<? extends GlobalApplicationEvent> listener) {
        if (listener != null) {
            ResolvableType resolvableType = ResolvableType
                    .forClass(GlobalApplicationEventListener.class, listener.getClass());
            MapUtils.getOrCreate(globalApplicationEventListeners,
                    resolvableType.getGeneric(0).getType(), ArrayList::new).add(listener);
        }
    }

    @Override
    public void publishEvent(GlobalApplicationEvent event) {
        redisPubSubService.convertAndMulticast(event.getName(), event);
    }

    @Override
    public void publishEvent(GlobalApplicationEvent event, long delay, TimeUnit timeUnit) {
        redisPubSubService.convertAndMulticast(event.getName(), event, delay, timeUnit);
    }

    @EventListener(RedisMessageEvent.class)
    public void onRedisMessageEvent(RedisMessageEvent event) {
        final Type type = event.getMessage().getClass();
        Optional.ofNullable(globalApplicationEventListeners.get(type)).ifPresent(listeners -> {
            listeners.forEach(listener -> {
                try {
                    MethodUtils.invokeExactMethod(listener, "onGlobalApplicationEvent",
                            event.getMessage());
                } catch (Throwable e) {
                    e = ExceptionUtils.getOriginalException(e);
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        });;
    }
}
