package lightfish.im.core.dto;

import java.net.InetSocketAddress;


public class BaseMsg {
    private final long userId;
    private final byte flag;
    private  String content;
    private  InetSocketAddress inetSocketAddress;

    public BaseMsg(long userId, byte flag, String content) {
        this.userId = userId;
        this.flag = flag;
        this.content = content;
    }

    public String getContent() {
        if(content==null)
        {
            return  null;
        }
        return content.trim();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public byte getFlag() {

        return flag;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

}
