package com.study.server.decoder;

import com.study.server.util.SerializationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return ;
        }
        in.markReaderIndex();

        int datalength = in.readInt();

        if(datalength < 0){
            ctx.close();
        }

        if(in.readableBytes() < datalength){
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[datalength];

        in.readBytes(data);

        Object obj = SerializationUtils.deserialize(data, genericClass);

        out.add(obj);
    }
}
