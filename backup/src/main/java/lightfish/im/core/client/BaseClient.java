package lightfish.im.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.swt.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


public class BaseClient {
    private static final Logger LOG = LoggerFactory.getLogger(BaseClient.class);
    final ChatClient chatClient;
    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    Channel channel;
    Thread keepliveThread;
    BaseMsg keepAliveMsg;

    public BaseClient(ChatClient chatClient) {
        super();
        this.chatClient = chatClient;

    }

    public void bind(final InetSocketAddress address) {
        LOG.info("client start!");
        bootstrap.group(group);
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.option(ChannelOption.SO_BROADCAST, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipe = ch.pipeline();
                pipe.addLast(new BaseEncoder(address));
                pipe.addLast(new BaseClientHandler(chatClient));

            }
        });
        channel = this.bootstrap.bind(0).syncUninterruptibly().channel();
        keepAliveMsg = new BaseMsg(chatClient.baseMsg.getUserId(), (byte)0, "");
        channel.writeAndFlush(new BaseMsg(chatClient.baseMsg.getUserId(), (byte)1, ""));
        channel.writeAndFlush(new BaseMsg(chatClient.baseMsg.getUserId(), (byte)0, ""));

        this.keepliveThread = new Thread() {

            @Override
            public void run() {
                for (; ; ) {
                    channel.writeAndFlush(keepAliveMsg);
                    try {
                        sleep(3000L);
                        System.out.println("心跳");
                    } catch (InterruptedException e) {

                    }
                }
            }
        };
        keepliveThread.start();
    }

    public void stop() {
        group.shutdownGracefully();
        LOG.info("teamSayClient exit");
    }

    public Channel getChannel() {
        return channel;
    }

}
