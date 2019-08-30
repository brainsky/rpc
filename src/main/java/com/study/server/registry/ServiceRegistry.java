package com.study.server.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ServiceRegistry {

    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * 服务注册地址
     */
    private  String  registryAddress;

    public ServiceRegistry(String registryAddress){
            this.registryAddress = registryAddress;
    }


    public void register(String data){
        if(StringUtils.isNotBlank(data)){
            ZooKeeper  zooKeeper = connectServer();
            if(zooKeeper != null){
                creatNode(zooKeeper, data);
            }
        }
    }

    private void creatNode(ZooKeeper zooKeeper, String data) {
    }

    private ZooKeeper connectServer() {



    }

}
