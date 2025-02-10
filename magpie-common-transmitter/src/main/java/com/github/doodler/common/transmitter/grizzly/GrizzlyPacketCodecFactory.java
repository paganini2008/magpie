package com.github.doodler.common.transmitter.grizzly;

import com.github.doodler.common.transmitter.grizzly.GrizzlyEncoderDecoderUtils.PacketDecoder;
import com.github.doodler.common.transmitter.grizzly.GrizzlyEncoderDecoderUtils.PacketEncoder;
import com.github.doodler.common.transmitter.serializer.KryoSerializer;
import com.github.doodler.common.transmitter.serializer.Serializer;

/**
 * 
 * @Description: GrizzlyPacketCodecFactory
 * @Author: Fred Feng
 * @Date: 09/01/2025
 * @Version 1.0.0
 */
public class GrizzlyPacketCodecFactory implements PacketCodecFactory {

    private final Serializer serializer;

    public GrizzlyPacketCodecFactory() {
        this(new KryoSerializer());
    }

    public GrizzlyPacketCodecFactory(Serializer serializer) {
        this.serializer = serializer;
    }

    public PacketEncoder getEncoder() {
        return new PacketEncoder(serializer);
    }

    public PacketDecoder getDecoder() {
        return new PacketDecoder(serializer);
    }

    public Serializer getSerializer() {
        return serializer;
    }

}
