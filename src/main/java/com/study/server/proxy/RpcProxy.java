package com.study.server.proxy;

import com.study.server.registry.ClientServiceRegistry;
import com.study.server.registry.ServiceRegistry;

public class RpcProxy {


    private ClientServiceRegistry serviceDiscovery;

    public RpcProxy(ClientServiceRegistry serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

}
