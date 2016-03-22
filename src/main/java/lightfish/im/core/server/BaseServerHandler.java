package lightfish.im.core.server;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import lightfish.im.core.actor.ResponseActor;
import lightfish.im.core.actorMsg.SessionMsg;
import lightfish.im.core.dto.BaseMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static lightfish.im.kit.SpringExtension.SpringExtProvider;

@Service
public class BaseServerHandler extends SimpleChannelInboundHandler<BaseMsg> {
    private static final Logger log = LoggerFactory.getLogger(BaseServerHandler.class);
    private ActorSystem system;
    ActorRef eventDispatcher;
    ActorRef session;
    ActorRef response;
    Cancellable cancellable;

    @Inject
    public void BaseServerHandler(ActorSystem system) {
        this.system = system;
        eventDispatcher = system.actorOf(Props.create(lightfish.im.core.actor.EventDispatcherActor.class), "eventDispatcher");
        response = system.actorOf(Props.create(ResponseActor.class), "response");
        session = system.actorOf(SpringExtProvider.get(system).props("IMSessionManagerActor"), "session");
        cancellable = system.scheduler().schedule(Duration.Zero(),
                Duration.create(2, TimeUnit.MILLISECONDS), session, "session",
                system.dispatcher(), null);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, BaseMsg msg) throws Exception {
        if (msg.getContent() == null && msg.getFlag() == 0) {
            session.tell(new SessionMsg(SessionMsg.Type.REFLASH, msg.getUserId().toString()), ActorRef.noSender());
            System.out.println("心跳 " + msg.getUserId());
            return;
        }
        eventDispatcher.tell(msg, ActorRef.noSender());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        BaseServer.CHANNELS.remove(ctx.channel());
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
        BaseServer.CHANNELS.add(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info("close");
        BaseServer.CHANNELS.remove(ctx.channel());
        super.close(ctx, promise);
    }
}
