package com.github.doodler.common.transmitter;

import static com.github.doodler.common.Constants.NEWLINE;
import static com.github.doodler.common.transmitter.PacketKeywords.INSTANCE_ID;
import static com.github.doodler.common.transmitter.TransmitterConstants.MODE_ASYNC;
import java.util.concurrent.TimeUnit;
import com.github.doodler.common.events.EventPublisher;
import com.github.doodler.common.utils.SimpleTimer;

/**
 * 
 * @Description: PerformanceInspector
 * @Author: Fred Feng
 * @Date: 29/01/2025
 * @Version 1.0.0
 */
public class PerformanceInspector extends SimpleTimer {

    private final EventPublisher<?> eventPublisher;
    private final NioContext nioContext;
    private final PerformanceInspectorService performanceInspectorService;

    public PerformanceInspector(long period, TimeUnit timeUnit, EventPublisher<?> eventPublisher,
            NioContext nioContext, PerformanceInspectorService performanceInspectorService) {
        super(period, timeUnit);
        this.eventPublisher = eventPublisher;
        this.nioContext = nioContext;
        this.performanceInspectorService = performanceInspectorService;
    }

    public void beforeConsuming(Object event) {
        Packet packet = (Packet) event;
        String instanceId = packet.getStringField(INSTANCE_ID);
        long startTime = packet.getTimestamp();
        performanceInspectorService.update(instanceId, MODE_ASYNC, System.currentTimeMillis(),
                s -> {
                    s.getSample().totalExecutions.increment();
                    s.getSample().timestamp = startTime;
                });
    }

    public void completeConsuming(Object event, Throwable e) {
        Packet packet = (Packet) event;
        String instanceId = packet.getStringField(INSTANCE_ID);
        long startTime = packet.getTimestamp();
        performanceInspectorService.update(instanceId, MODE_ASYNC, System.currentTimeMillis(),
                s -> {
                    if (e != null) {
                        s.getSample().failedExecutions.increment();
                    }
                    s.getSample().accumulatedExecutionTime
                            .add(System.currentTimeMillis() - startTime);
                });
    }

    private int idleTimeout = 15000;

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Override
    public boolean change() throws Exception {
        if (eventPublisher.getEstimatedLagAmount() > 0
                || eventPublisher.remainingBufferSize() > 0) {
            StringBuilder str = new StringBuilder();
            str.append("[EventPublisher]: estimatedLagAmount: "
                    + eventPublisher.getEstimatedLagAmount())
                    .append(", remainingBufferSize: " + eventPublisher.remainingBufferSize())
                    .append(", concurrents: " + nioContext.getConcurrents());
            if (log.isInfoEnabled()) {
                log.info(str.toString());
            }
        }
        performanceInspectorService.categories().forEach(cat -> {
            performanceInspectorService.summarize(cat).entrySet().forEach(e -> {
                long lastModified = e.getValue().getSample().timestamp;
                if (idleTimeout > 0 && System.currentTimeMillis() - lastModified > idleTimeout) {
                    StringBuilder logStr = new StringBuilder();
                    logStr.append(NEWLINE).append("Instance: " + cat);
                    logStr.append(NEWLINE).append("Mode: " + e.getKey());
                    logStr.append(NEWLINE).append(e.getValue().getSample().toString());
                    if (log.isInfoEnabled()) {
                        log.info(logStr.toString());
                    }
                }
            });
        });
        return true;
    }

}
