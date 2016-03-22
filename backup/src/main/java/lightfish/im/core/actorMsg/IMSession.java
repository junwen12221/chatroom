package lightfish.im.core.actorMsg;

import java.net.InetSocketAddress;

/**
 * Created by karak on 16-3-16.
 */
public class IMSession {
    private   String id;
    private InetSocketAddress inetSocketAddress;

    public IMSession(String id, InetSocketAddress inetSocketAddress) {
        this.id = id;
        this.inetSocketAddress = inetSocketAddress;
    }

    public String getId() {

        return id;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }
}
