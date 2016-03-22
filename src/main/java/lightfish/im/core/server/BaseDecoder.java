package lightfish.im.core.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.kit.ByteKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BaseDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        LOG.info("decode");
        ByteBuf buf = msg.content();
        int readable = buf.readableBytes();
        long userId = buf.readLong();

        byte  flag= buf.readByte();

        String content = buf.slice(6, readable - 6).toString(CharsetUtil.UTF_8);
        BaseMsg baseMsg;
        if (flag==(byte)0&&content.trim().equals("")) {
            baseMsg = new BaseMsg(userId, (byte) 0, null);
            baseMsg.setInetSocketAddress(msg.sender());
        } else {
            baseMsg = new BaseMsg(userId,(byte) 0, content);
            baseMsg.setInetSocketAddress(msg.sender());
        }
        out.add(baseMsg);
    }


}
