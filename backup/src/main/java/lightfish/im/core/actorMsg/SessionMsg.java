package lightfish.im.core.actorMsg;

import java.net.InetSocketAddress;

public class SessionMsg extends IMSession  {
    private Type type;


    public SessionMsg(Type type, String id,InetSocketAddress inetSocketAddress) {

       super(id,inetSocketAddress);
        this.type = type;
    }
    public SessionMsg(Type type, String id) {

        super(id,null);
        this.type = type;
    }


    public Type getType() {

        return type;
    }


    public enum Type {
        REFLASH,
        COUNT,
        REMOVE,
        ACTIVESESSIONS
    }

}