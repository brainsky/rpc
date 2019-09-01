package com.study.server.registry;

import com.study.server.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;

import java.io.IOException;
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


    /**
     * 连接zookeeper服务中心
     * @return
     */
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, (event)->{
                    if(event.getState().equals(Watcher.Event.KeeperState.SyncConnected)){
                        latch.countDown();
                    }
            });

            latch.await();
        } catch (IOException | InterruptedException exception) {
           log.error("connectServer exception:{}",exception);
        }

        return zk;
    }

    /**
     * zookeeper创建节点
     * @param zooKeeper
     * @param data
     */
    private void creatNode(ZooKeeper zooKeeper, String data) {

        byte[] bytes = data.getBytes();

        try {
            String path = zooKeeper.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            log.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            log.error("creatNode exception",e);
        }


    }

}
