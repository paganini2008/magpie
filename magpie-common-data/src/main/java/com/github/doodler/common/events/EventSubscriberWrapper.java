package com.github.doodler.common.events;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: EventSubscriberWrapper
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
@Slf4j
public class EventSubscriberWrapper<T> implements Flow.Subscriber<T> {

    private final EventSubscriber<T> subscriber;
    private final Executor executor;
    private final int requestFetchSize;
    private final Context context;
    private final EventPublisher<T> eventPublisher;

    EventSubscriberWrapper(EventSubscriber<T> subscriber, Executor executor, int requestFetchSize,
            Context context, EventPublisher<T> eventPublisher) {
        this.subscriber = subscriber;
        this.executor = Optional.ofNullable(executor).orElse(ForkJoinPool.commonPool());
        this.requestFetchSize = requestFetchSize;
        this.context = context;
        this.eventPublisher = eventPublisher;
    }

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(requestFetchSize);
        this.subscription = subscription;
    }

    @Override
    public void onNext(T item) {
        CompletableFuture.runAsync(() -> {
            subscriber.consume(item, context);
        }, executor).exceptionally(e -> {
            subscriber.onError(item, e, context);
            return null;
        }).whenComplete((result, e) -> {
            subscriber.onComplete(item, e, context);
            subscription.request(adjustRequestFetchSize());
        });
    }

    private long adjustRequestFetchSize() {
        if (requestFetchSize > 1) {
            long lag = eventPublisher.getEstimatedLagAmount();
            if (lag > eventPublisher.getMaxBufferCapacity() * 0.8) {
                return Math.max(1, requestFetchSize / 2);
            } else if (lag < eventPublisher.getMaxBufferCapacity() * 0.2) {
                return Math.min(eventPublisher.getMaxBufferCapacity() / 10, requestFetchSize * 2);
            }
        }
        return requestFetchSize;
    }

    @Override
    public void onError(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onComplete() {
        if (log.isInfoEnabled()) {
            log.info("Completed");
        }
    }
}
