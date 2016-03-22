package lightfish.im.core.client;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lightfish.im.swt.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseClientHandler.class);
    final ChatClient chatClient;
    
    public BaseClientHandler(ChatClient chatClient) {
        super();
        this.chatClient = chatClient;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf byteBuf=msg.content();
        LOG.info(msg.content().toString(CharsetUtil.UTF_8));
        chatClient.showSayMsg(msg.content().toString(CharsetUtil.UTF_8));
    }
}
