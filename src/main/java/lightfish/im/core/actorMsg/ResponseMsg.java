package lightfish.im.core.actorMsg;

import java.net.InetSocketAddress;

/**
 * Created by karak on 16-3-16.
 */
public class ResponseMsg {
   public final String text;
    public final InetSocketAddress inetSocketAddress;

    public ResponseMsg(String text, InetSocketAddress inetSocketAddress) {
        this.text = text;
        this.inetSocketAddress = inetSocketAddress;
    }

    public String getText() {
        return text;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

}
