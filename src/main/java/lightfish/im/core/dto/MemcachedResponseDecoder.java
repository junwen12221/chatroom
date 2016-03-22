package lightfish.im.core.dto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class MemcachedResponseDecoder extends ByteToMessageDecoder {  //1
    private enum State {  //2
        Header,
        Body
    }

    private State state = State.Header;
    private int totalBodySize;
    private byte magic;
    private byte opCode;
    private short keyLength;
    private byte extraLength;
    private short status;
    private int id;
    private long cas;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) {
        switch (state) { //3
            case Header:
                if (in.readableBytes() < 24) {
                    return;//response header is 24  bytes  //4
                }
                magic = in.readByte();  //5
                opCode = in.readByte();
                keyLength = in.readShort();
                extraLength = in.readByte();
                in.skipBytes(1);
                status = in.readShort();
                totalBodySize = in.readInt();
                id = in.readInt(); //referred to in the protocol spec as opaque
                cas = in.readLong();

                state = State.Body;
            case Body:
                if (in.readableBytes() < totalBodySize) {
                    return; //until we have the entire payload return  //6
                }
                int flags = 0, expires = 0;
                int actualBodySize = totalBodySize;
                if (extraLength > 0) {  //7
                    flags = in.readInt();
                    actualBodySize -= 4;
                }
                if (extraLength > 4) {  //8
                    expires = in.readInt();
                    actualBodySize -= 4;
                }
                String key = "";
                if (keyLength > 0) {  //9
                    ByteBuf keyBytes = in.readBytes(keyLength);
                    key = keyBytes.toString(CharsetUtil.UTF_8);
                    actualBodySize -= keyLength;
                }
                ByteBuf body = in.readBytes(actualBodySize);  //10
                String data = body.toString(CharsetUtil.UTF_8);
                out.add(new MemcachedResponse(
                        magic,
                        opCode,
                        (byte)0x00,
                        status,
                        id,
                        cas,
                        flags,
                        expires,
                        key,
                        data
                ));
                state = State.Header;
        }

    }
}