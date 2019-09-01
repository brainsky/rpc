package com.study.server.handler;

import com.study.server.domain.RpcRequest;
import com.study.server.domain.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.Map;

@Slf4j
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String,Object> handleMap;


    public RpcHandler(Map<String,Object> handleMap){
        this.handleMap = handleMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Throwable e) {
            response.setError(e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private Object handle(RpcRequest request) throws Throwable{
        String className = request.getClassName();
        Object serviceBean = handleMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();

        String methodName = request.getMethodName();

        Class<?>[] parameterTypes = request.getParameterTypes();

        Object[] parameters = request.getParameters();

        FastClass serviceFastClass = FastClass.create(serviceClass);

        FastMethod  serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

        return serviceFastMethod.invoke(serviceBean, parameters);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught", cause);
        ctx.close();
    }


}
