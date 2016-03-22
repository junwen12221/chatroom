package lightfish.im;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import lightfish.im.core.server.BaseServer;
import lightfish.im.kit.SentinelJedisUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static sample.SpringExtension.SpringExtProvider;

/**
 * Created by karak on 16-3-14.
 */
public class Main {
    public   static   AnnotationConfigApplicationContext CTX;
    public static void main(String[] args) {
        // create a spring context and scan the classes
        new SentinelJedisUtil().init();
        CTX = new AnnotationConfigApplicationContext();
        CTX.scan("lightfish");
        CTX.refresh();
        ActorSystem system = CTX.getBean(ActorSystem.class);

        //ActorRef counter = system.actorOf(SpringExtProvider.get(system).props("CountingActor"), "counter");
        BaseServer b=  new BaseServer();
         b.bind();
        return;
    }
}
