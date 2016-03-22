package lightfish.im.core.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lightfish.im.core.actorMsg.ResponseMsg;
import lightfish.im.core.actorMsg.SessionMsg;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.core.server.BaseServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by karak on 16-3-15.
 */
public class EventDispatcherActor extends UntypedActor {
    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    static Set<InetSocketAddress> addresses = new HashSet<InetSocketAddress>();
    ActorSelection session= this.getContext().actorSelection("../session");
    ActorRef security=this.getContext().actorOf(Props.create(SecurityActor.class),"security");
    ActorSelection response = this.getContext().actorSelection("../response");
    ActorRef broadcast=this.getContext().actorOf(Props.create(BroadcastActor.class),"broadcast");
    @Override
    public void onReceive(Object message) throws Exception {
        if(!(message instanceof  BaseMsg))
        {
            unhandled(message);
            return;
        }
        BaseMsg msg = (BaseMsg) message;
        switch (msg.getFlag())
        {
            case 1:
                security.tell(msg,ActorRef.noSender());
                //session.tell(new SessionMsg(SessionMsg.Type.REFLASH, msg.getUserId().toString()), ActorRef.noSender());
                log.info("登录: " + msg.getUserId()) ;
                break;
            case 2://单对单
                response.tell(msg,ActorRef.noSender());
                break;
            case 3://全体广播
                broadcast.tell(msg,ActorRef.noSender());
                break;
            case 4://聊天室
                ActorRef chatroom=  this.getContext().actorOf(Props.create(ChatroomActor.class),"chatroom");
                chatroom.tell(msg,ActorRef.noSender());
                break;
            default:
                break;
        }


        // 服务端转发给所有客户端
        StringBuilder text = new StringBuilder();
        text.append("[");
        text.append(msg.getUserId());
        text.append("|");
        text.append("]：");
        String content = msg.getContent();
        text.append(content);

        for (InetSocketAddress recipient : addresses) {
            ResponseMsg m = new ResponseMsg(text.toString(),recipient);
            response.tell(m, ActorRef.noSender());
        }
    }
}
