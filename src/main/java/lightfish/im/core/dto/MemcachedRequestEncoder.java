package lightfish.im.core.dto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MemcachedRequestEncoder extends
        MessageToByteEncoder<MemcachedRequest> { //1
    @Override
    protected void encode(ChannelHandlerContext ctx, MemcachedRequest msg,
                          ByteBuf out) throws Exception {  //2
        byte[] key = msg.key().getBytes(CharsetUtil.UTF_8);
        byte[] body = msg.body().getBytes(CharsetUtil.UTF_8);
        //total size of the body = key size + content size + extras size   //3
        int bodySize = key.length + body.length + (msg.hasExtras() ? 8 : 0);

        //write magic byte  //4
        out.writeByte(msg.magic());
        //write opcode byte  //5
        out.writeByte(msg.opCode());
        //write key length (2 byte) //6
        out.writeShort(key.length); //key length is max 2 bytes i.e. a Java short  //7
        //write extras length (1 byte)
        int extraSize =  msg.hasExtras() ? 0x08 : 0x0;
        out.writeByte(extraSize);
        //byte is the data type, not currently implemented in Memcached but required //8
        out.writeByte(0);
        //next two bytes are reserved, not currently implemented but are required  //9
        out.writeShort(0);

        //write total body length ( 4 bytes - 32 bit int)  //10
        out.writeInt(bodySize);
        //write opaque ( 4 bytes)  -  a 32 bit int that is returned in the response //11
        out.writeInt(msg.id());

        //write CAS ( 8 bytes)
        out.writeLong(msg.cas());   //24 byte header finishes with the CAS  //12

        if (msg.hasExtras()) {
            //write extras (flags and expiry, 4 bytes each) - 8 bytes total  //13
            out.writeInt(msg.flags());
            out.writeInt(msg.expires());
        }
        //write key   //14
        out.writeBytes(key);
        //write value  //15
        out.writeBytes(body);
    }
}