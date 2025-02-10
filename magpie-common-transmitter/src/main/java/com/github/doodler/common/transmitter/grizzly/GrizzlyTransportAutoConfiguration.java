package com.github.doodler.common.transmitter.grizzly;

import java.util.concurrent.TimeUnit;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.transmitter.ChannelEventListener;
import com.github.doodler.common.transmitter.ChannelSwitcher;
import com.github.doodler.common.transmitter.NioClient;
import com.github.doodler.common.transmitter.NioServer;
import com.github.doodler.common.transmitter.TransmitterNioProperties;
import com.github.doodler.common.transmitter.serializer.Serializer;

/**
 * 
 * @Description: GrizzlyTransportAutoConfiguration
 * @Author: Fred Feng
 * @Date: 13/01/2025
 * @Version 1.0.0
 */
@ConditionalOnClass({TCPNIOTransport.class})
@ConditionalOnProperty(name = "doodler.transmitter.nio.selection", havingValue = "grizzly")
@Configuration(proxyBeanMethods = false)
public class GrizzlyTransportAutoConfiguration {

    @Autowired
    private TransmitterNioProperties nioProperties;

    @Bean(initMethod = "open", destroyMethod = "close")
    public NioClient nioClient() {
        GrizzlyClient grizzlyClient = new GrizzlyClient();
        grizzlyClient.watchConnection(nioProperties.getClient().getReconnectInterval(),
                TimeUnit.SECONDS, nioProperties.getClient().getMaxReconnectAttempts());
        return grizzlyClient;
    }

    @Bean
    public NioServer nioServer() {
        return new GrizzlyServer();
    }

    @ConditionalOnMissingBean
    @Bean
    public PacketCodecFactory codecFactory(Serializer serializer) {
        return new GrizzlyPacketCodecFactory(serializer);
    }

    @Bean
    public GrizzlyServerHandler serverHandler() {
        return new GrizzlyServerHandler();
    }

    @Bean
    public ChannelEventListener<Connection<?>> channelEventListener() {
        return new GrizzlyChannelEventListener();
    }

    @Bean
    public ChannelEventListener<Connection<?>> cleanChannelEventListener(
            ChannelSwitcher channelSwitch) {
        return new CleanChannelEventListener(channelSwitch);
    }
}
