package com.github.doodler.common.transmitter.netty;

import java.net.SocketAddress;
import com.github.doodler.common.transmitter.ChannelEvent;
import com.github.doodler.common.transmitter.ChannelEvent.EventType;
import com.github.doodler.common.transmitter.ChannelEventListener;
import com.github.doodler.common.transmitter.ChannelSwitcher;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: CleanChannelEventListener
 * @Author: Fred Feng
 * @Date: 15/01/2025
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class CleanChannelEventListener implements ChannelEventListener<Channel> {

    private final ChannelSwitcher channelSwitch;

    @Override
    public void fireChannelEvent(ChannelEvent<Channel> channelEvent) {
        if (!channelEvent.isServerSide() && (channelEvent.getEventType() == EventType.CLOSED
                || channelEvent.getEventType() == EventType.ERROR)) {
            channelSwitch.remove((SocketAddress) channelEvent.getSource().remoteAddress());
        }
    }



}
