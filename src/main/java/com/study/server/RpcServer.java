package com.study.server;

import com.study.server.registry.ServiceRegistry;

public class RpcServer {

    private String serverAddress;

    private ServiceRegistry serviceRegistry;

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry){
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }
}
