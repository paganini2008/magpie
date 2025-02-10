package com.github.doodler.common.transmitter.netty;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.doodler.common.transmitter.ChannelEventListener;
import com.github.doodler.common.transmitter.MessageCodecFactory;
import com.github.doodler.common.transmitter.NioClient;
import com.github.doodler.common.transmitter.NioServer;
import com.github.doodler.common.transmitter.TransmitterNioProperties;
import com.github.doodler.common.transmitter.serializer.Serializer;
import io.netty.channel.Channel;

/**
 * 
 * @Description: NettyTransportAutoConfiguration
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "doodler.transmitter.nio.selection", havingValue = "netty",
        matchIfMissing = true)
public class NettyTransportAutoConfiguration {

    @Autowired
    private TransmitterNioProperties nioProperties;

    @Bean(initMethod = "open", destroyMethod = "close")
    public NioClient nioClient() {
        NettyClient nettyClient = new NettyClient();
        nettyClient.watchConnection(nioProperties.getClient().getReconnectInterval(),
                TimeUnit.SECONDS, nioProperties.getClient().getMaxReconnectAttempts());
        return nettyClient;
    }

    @Bean
    public NioServer nioServer() {
        return new NettyServer();
    }

    @ConditionalOnMissingBean
    @Bean
    public KeepAlivePolicy keepAlivePolicy() {
        return new NettyServerKeepAlivePolicy();
    }

    @Bean
    public MessageCodecFactory codecFactory(Serializer serializer) {
        return new NettyMessageCodecFactory(serializer);
    }

    @Bean
    public NettyServerHandler serverHandler() {
        return new NettyServerHandler();
    }

    @Bean
    public ChannelEventListener<Channel> channelEventListener() {
        return new NettyChannelEventListener();
    }
}
