package com.github.doodler.common.transmitter.mina;

import static com.github.doodler.common.transmitter.TransmitterConstants.MODE_SYNC;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.doodler.common.events.EventPublisher;
import com.github.doodler.common.transmitter.ChannelEvent;
import com.github.doodler.common.transmitter.ChannelEvent.EventType;
import com.github.doodler.common.transmitter.ChannelEventListener;
import com.github.doodler.common.transmitter.Packet;
import com.github.doodler.common.transmitter.PacketHandlerExecution;
import com.github.doodler.common.transmitter.PerformanceInspectorService;
import com.github.doodler.common.utils.ExceptionUtils;
import com.github.doodler.common.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: MinaServerHandler
 * @Author: Fred Feng
 * @Date: 08/01/2025
 * @Version 1.0.0
 */
@Slf4j
public class MinaServerHandler extends IoHandlerAdapter {

    @Autowired
    private EventPublisher<Packet> eventPublisher;

    @Autowired
    private PerformanceInspectorService performanceInspector;

    @Autowired(required = false)
    private ChannelEventListener<IoSession> channelEventListener;

    @Autowired
    private PacketHandlerExecution packetHandlerExecution;

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        fireChannelEvent(session, EventType.CONNECTED, null);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        fireChannelEvent(session, EventType.CLOSED, null);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        fireChannelEvent(session, EventType.ERROR, cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (MODE_SYNC.equalsIgnoreCase(((Packet) message).getMode())) {
            Packet result = ((Packet) message).copy();
            long timestamp = result.getTimestamp();
            String instanceId = result.getStringField("instanceId");
            try {
                Object returnData = packetHandlerExecution.executeHandlerChain(result);
                if (returnData != null) {
                    if (returnData instanceof Packet) {
                        result = (Packet) returnData;
                    } else {
                        result.setObject(returnData);
                    }
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
                result.setField("errorMsg", e.getMessage());
                result.setField("errorDetails", ExceptionUtils.toString(e));
            } finally {
                final Packet tmp = result;
                performanceInspector.update(instanceId, MODE_SYNC, timestamp, s -> {
                    s.getSample().accumulatedExecutionTime
                            .add(System.currentTimeMillis() - timestamp);
                    s.getSample().totalExecutions.increment();
                    s.getSample().timestamp = System.currentTimeMillis();
                    if (tmp.hasField("errorMsg") || tmp.hasField("errorDetails")) {
                        s.getSample().failedExecutions.increment();
                    }
                });
            }
            result.setField("server", session.getLocalAddress().toString());
            result.setField("salt", IdUtils.getShortUuid());
            session.write(result);
        } else {
            eventPublisher.publish((Packet) message);
        }
    }

    private void fireChannelEvent(IoSession channel, EventType eventType, Throwable cause) {
        if (channelEventListener != null) {
            channelEventListener
                    .fireChannelEvent(new ChannelEvent<IoSession>(channel, eventType, true, cause));
        }
    }

}
