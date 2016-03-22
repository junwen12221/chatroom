package lightfish.im.core.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lightfish.im.core.actorMsg.IMSession;
import lightfish.im.core.actorMsg.InetSocketAddressList;
import lightfish.im.core.actorMsg.ResponseMsg;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.core.server.BaseServer;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by karak on 16-3-15.
 */
public class BroadcastActor extends UntypedActor {
    ActorSelection response = this.getContext().actorSelection("../response");
    ActorSelection session= this.getContext().actorSelection("../session");
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BaseMsg){
            BaseMsg msg=(BaseMsg)message;


        }else if(message instanceof Collection){
           for(IMSession i :((Collection<IMSession> ) message)){
                response.tell(i, ActorRef.noSender());
            }
        }else{
            unhandled(message);
        }
    }
}
