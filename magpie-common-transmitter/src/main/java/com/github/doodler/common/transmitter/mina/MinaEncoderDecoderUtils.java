package com.github.doodler.common.transmitter.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import com.github.doodler.common.transmitter.Packet;
import com.github.doodler.common.transmitter.TransmitterClientException;
import com.github.doodler.common.transmitter.serializer.Serializer;

/**
 * 
 * @Description: MinaEncoderDecoderUtils
 * @Author: Fred Feng
 * @Date: 09/01/2025
 * @Version 1.0.0
 */
public abstract class MinaEncoderDecoderUtils {

    public static class PacketEncoder extends ProtocolEncoderAdapter {
        private final Serializer serializer;
        private int maxObjectSize = Integer.MAX_VALUE;

        public PacketEncoder(Serializer serializer) {
            this.serializer = serializer;
        }

        public int getMaxObjectSize() {
            return maxObjectSize;
        }

        public void setMaxObjectSize(int maxObjectSize) {
            if (maxObjectSize <= 0) {
                throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
            }
            this.maxObjectSize = maxObjectSize;
        }

        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
                throws Exception {
            if (!(message instanceof Packet)) {
                return;
            }
            byte[] data = serializer.serialize((Packet) message);
            IoBuffer buf = IoBuffer.allocate(128).setAutoExpand(true);
            buf.putInt(data.length);
            buf.put(data);

            int objectSize = buf.position() - 4;
            if (objectSize > maxObjectSize) {
                throw new IllegalArgumentException("The encoded object is too big: " + objectSize
                        + " (> " + maxObjectSize + ')');
            }
            buf.flip();
            out.write(buf);
        }
    }

    public static class PacketDecoder extends CumulativeProtocolDecoder {

        private final Serializer serializer;
        private int maxObjectSize = Integer.MAX_VALUE;

        public PacketDecoder(Serializer serializer) {
            this.serializer = serializer;
        }

        public int getMaxObjectSize() {
            return maxObjectSize;
        }

        public void setMaxObjectSize(int maxObjectSize) {
            if (maxObjectSize <= 0) {
                throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
            }
            this.maxObjectSize = maxObjectSize;
        }

        @Override
        protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
                throws Exception {
            if (!in.prefixedDataAvailable(4, maxObjectSize)) {
                return false;
            }
            int dataLength = in.getInt();
            if (dataLength < 4) {
                throw new TransmitterClientException(
                        "Data length should be greater than 4: " + dataLength);
            }
            byte[] data = new byte[dataLength];
            in.get(data);
            Packet packet = serializer.deserialize(data);
            out.write(packet);
            return true;
        }

    }

}
