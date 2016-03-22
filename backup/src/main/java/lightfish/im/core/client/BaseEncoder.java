package lightfish.im.core.client;

import lightfish.im.core.dto.BaseMsg;
import lightfish.im.kit.ByteKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.List;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEncoder extends MessageToMessageEncoder<BaseMsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEncoder.class);

    private final InetSocketAddress recipient;

    public BaseEncoder(InetSocketAddress recipient) {
        this.recipient = recipient;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMsg msg, List<Object> out) throws Exception {
        LOG.info("encode");
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(ByteKit.toBytes(msg.getUserId()));
        buf.writeByte(msg.getFlag());
        buf.writeBytes(ByteKit.toBytes(msg.getContent()));
        out.add(new DatagramPacket(buf, this.recipient));
    }

}
