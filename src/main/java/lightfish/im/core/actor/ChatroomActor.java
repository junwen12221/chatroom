package lightfish.im.core.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karak on 16-3-15.
 */
public class ChatroomActor extends UntypedActor {
    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    public Router router;



    @Override
    public void onReceive(Object message) throws Exception {
    }
}
