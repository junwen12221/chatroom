package lightfish.im.core.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lightfish.im.Config;
import lightfish.im.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseServer {
    private static final Logger LOG = LoggerFactory.getLogger(BaseServer.class);
    public static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Channel channel;
    public void bind() {
        LOG.info("BaseServer start !");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.option(ChannelOption.SO_BROADCAST, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipe = ch.pipeline();
                pipe.addLast(new BaseDecoder());
                pipe.addLast(Main.CTX.getBean(BaseServerHandler.class));

            }
        });
        try {
            channel = bootstrap.bind(Config.getInatAddress(), Config.getPORT()).sync().channel();
            channel.closeFuture().await();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public static Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) {

        BaseServer b = new BaseServer();
        b.bind();

    }
}
