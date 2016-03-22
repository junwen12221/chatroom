package lightfish.im.core.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lightfish.im.core.actorMsg.ResponseMsg;
import lightfish.im.core.dto.BaseMsg;

/**
 * Created by karak on 16-3-16.
 */
public class SecurityActor extends UntypedActor {
    ActorSelection session= this.getContext().actorSelection("../session");
    ActorSelection response = this.getContext().actorSelection("../response");
    @Override
    public void onReceive(Object message) throws Exception {
      if(message instanceof BaseMsg)
      {
          BaseMsg msg=(BaseMsg)message;
          session.tell(msg,getSelf());
          response.tell(new ResponseMsg("登录成功",msg.getInetSocketAddress()), ActorRef.noSender());
        }else {
          unhandled(message);
      }
    }
}
