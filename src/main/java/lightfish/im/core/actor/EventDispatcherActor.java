package lightfish.im.core.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lightfish.im.core.actorMsg.ResponseMsg;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.core.server.BaseServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by karak on 16-3-15.
 */
public class EventDispatcherActor extends UntypedActor {
    static Set<InetSocketAddress> addresses = new HashSet<InetSocketAddress>();

    @Override
    public void onReceive(Object message) throws Exception {
        BaseMsg msg = (BaseMsg) message;
        Channel channel = BaseServer.getChannel();
        if (!addresses.contains(msg.getInetSocketAddress())) {
            addresses.add(msg.getInetSocketAddress());
            String info = msg.getInetSocketAddress().toString() + "当前在线人数:" + addresses.size();
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(info, CharsetUtil.UTF_8), msg.getInetSocketAddress()));
        }

        // 服务端转发给所有客户端
        StringBuilder text = new StringBuilder();
        text.append("[");
        text.append(msg.getUserId());
        text.append("|");
        text.append("]：");
        String content = msg.getContent();
        text.append(content);
        //LOG.info("messageReceived: " + text.toString());


        ActorSelection response = this.getContext().actorSelection("../response");


        for (InetSocketAddress recipient : addresses) {
            ResponseMsg m = new ResponseMsg(text.toString(),recipient);
            response.tell(m, ActorRef.noSender());
        }
    }
}
