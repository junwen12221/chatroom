package lightfish.im.core.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lightfish.im.core.actorMsg.ResponseMsg;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.core.server.BaseServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

/**
 * Created by karak on 16-3-15.
 */
@Named
@Scope("prototype")
public class ResponseActor extends UntypedActor {
    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ResponseMsg){
            ResponseMsg responseMsg=  (ResponseMsg)message;
            DatagramPacket data = new DatagramPacket(Unpooled.copiedBuffer(responseMsg.text, CharsetUtil.UTF_8), responseMsg.inetSocketAddress);
            BaseServer.getChannel().writeAndFlush(data);
        }else{
            unhandled(message);
        }

    }
}
