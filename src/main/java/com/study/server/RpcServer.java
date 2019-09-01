package com.study.server;

import com.study.server.anotation.RpcService;
import com.study.server.decoder.RpcDecoder;
import com.study.server.encoder.RpcEncoder;
import com.study.server.handler.RpcHandler;
import com.study.server.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serverAddress;

    private ServiceRegistry serviceRegistry;

    // 存放接口名与服务对象之间的映射关系
    private Map<String,Object> handleMap = new HashMap<>();

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry){
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);

        if(MapUtils.isNotEmpty(serviceBeanMap)){
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handleMap.put(interfaceName,serviceBean);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder(RpcDecoder.class))
                                    .addLast(new RpcEncoder(RpcEncoder.class))
                                    .addLast(new RpcHandler(handleMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(".");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();

            log.info("server start on port:{}", port);

            if(serviceRegistry != null){
                serviceRegistry.register(serverAddress); //注册服务地址
            }

            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
