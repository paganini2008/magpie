package com.github.doodler.common.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import org.apache.commons.collections4.CollectionUtils;
import com.github.doodler.common.utils.ExecutorUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: EventPublisherImpl
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
@Slf4j
public class EventPublisherImpl<T>
        implements BiConsumer<Flow.Subscriber<? super T>, Throwable>, EventPublisher<T> {

    private final SubmissionPublisher<T> publisher;
    private final int requestFetchSize;
    private final long timeout;
    private final Buffer<T> buffer;
    private final BufferCleaner bufferCleaner;
    private final Executor subExecutor;
    private Context context;
    private List<Executor> internalExecutors = new ArrayList<>();

    public EventPublisherImpl(Executor pubExecutor, Executor subExecutor, int maxBufferCapacity,
            int requestFetchSize, long timeout, Buffer<T> buffer, long bufferCleanInterval) {
        if (pubExecutor == null) {
            pubExecutor = Executors.newCachedThreadPool(
                    new ThreadFactoryBuilder().setNameFormat("event-pub-pool-%d").build());
            internalExecutors.add(pubExecutor);
        }
        if (subExecutor == null) {
            int cpuCores = Runtime.getRuntime().availableProcessors();
            int nThreads = Math.max(cpuCores * 2, maxBufferCapacity / requestFetchSize);
            subExecutor = Executors.newFixedThreadPool(nThreads,
                    new ThreadFactoryBuilder().setNameFormat("event-sub-pool-%d").build());
            internalExecutors.add(subExecutor);
        }
        this.publisher = new SubmissionPublisher<>(pubExecutor, maxBufferCapacity, this);
        this.requestFetchSize = requestFetchSize;
        this.timeout = timeout;
        this.buffer = buffer;
        this.bufferedCleanerEnabled = true;
        this.bufferCleaner = new BufferCleaner(bufferCleanInterval);
        this.subExecutor = subExecutor;
        this.context = new Context();
    }

    private volatile boolean bufferedCleanerEnabled;

    @Override
    public void enableBufferCleaner(boolean enabled) {
        this.bufferedCleanerEnabled = enabled;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int subscribe(Collection<EventSubscriber<T>> subscribers) {
        if (CollectionUtils.isNotEmpty(subscribers)) {
            for (EventSubscriber<T> subscriber : subscribers) {
                publisher.subscribe(new EventSubscriberWrapper<T>(subscriber, subExecutor,
                        requestFetchSize, context, this));
            }
        }
        return publisher.getNumberOfSubscribers();
    }

    @Override
    public void publish(T event) {
        publisher.offer(event, timeout, TimeUnit.MILLISECONDS, (sub, droppedItem) -> {
            if (buffer != null) {
                buffer.put(droppedItem);
                return true;
            }
            return false;
        });
    }

    @Override
    public long getMaxBufferCapacity() {
        return publisher.getMaxBufferCapacity();
    }

    @Override
    public long getEstimatedLagAmount() {
        return publisher.estimateMaximumLag();
    }

    @Override
    public long remainingBufferSize() {
        return buffer.size();
    }

    @Override
    public void destroy() {
        if (bufferCleaner != null) {
            bufferCleaner.stop();
        }
        if (buffer != null) {
            buffer.destroy();
        }
        for (Executor executor : internalExecutors) {
            ExecutorUtils.gracefulShutdown(executor, 60000L);
        }
    }

    @Override
    public void accept(Subscriber<? super T> s, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("Error occurred in subscriber: {}", s);
            log.error("Error occurred: {}", e.getMessage(), e);
        }
    }

    /**
     * 
     * @Description: BufferCleaner
     * @Author: Fred Feng
     * @Date: 29/12/2024
     * @Version 1.0.0
     */
    private class BufferCleaner extends TimerTask {

        private final Timer timer;

        BufferCleaner(long bufferCleanInterval) {
            timer = new Timer("BufferCleaner");
            timer.schedule(this, bufferCleanInterval, bufferCleanInterval);
        }

        public void stop() {
            if (timer != null) {
                timer.cancel();
            }
        }

        @Override
        public void run() {
            if (!bufferedCleanerEnabled || buffer.size() == 0) {
                return;
            }
            long maxBufferCapacity = publisher.getMaxBufferCapacity();
            long lag = getEstimatedLagAmount();
            if (log.isTraceEnabled()) {
                log.trace("EventPublisher performance metrics: {}/{}/{}", lag, maxBufferCapacity,
                        buffer.size());
            }
            if (lag + requestFetchSize > maxBufferCapacity) {
                return;
            }
            Collection<T> results;
            if (lag < maxBufferCapacity * 0.1) {
                results = buffer.poll(Math.min(maxBufferCapacity, buffer.size()));
            } else {
                results = buffer.poll(requestFetchSize);
            }

            if (CollectionUtils.isNotEmpty(results)) {
                results.forEach(event -> {
                    publish(event);
                });
            }
        }

    }

}
