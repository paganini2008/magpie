package com.github.doodler.common.transmitter.grizzly;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.AbstractCodecFilter;
import com.github.doodler.common.transmitter.Packet;
import com.github.doodler.common.transmitter.serializer.KryoSerializer;
import com.github.doodler.common.transmitter.serializer.Serializer;

/**
 * 
 * @Description: PacketFilter
 * @Author: Fred Feng
 * @Date: 08/01/2025
 * @Version 1.0.0
 */
public class PacketFilter extends AbstractCodecFilter<Buffer, Packet> {

    public PacketFilter() {
        this(new KryoSerializer());
    }

    public PacketFilter(Serializer serializer) {
        this(new GrizzlyPacketCodecFactory(serializer));
    }

    public PacketFilter(PacketCodecFactory codecFactory) {
        super(codecFactory.getDecoder(), codecFactory.getEncoder());
    }

}
