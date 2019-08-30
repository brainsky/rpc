package com.study.server.impl;

import com.study.server.anotation.RpcService;
import com.study.server.iface.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello "+ name +"!";
    }
}
