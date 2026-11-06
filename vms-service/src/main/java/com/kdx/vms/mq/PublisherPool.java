package com.kdx.vms.mq;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class PublisherPool {
    GenericObjectPool<Publisher> pool;
    private PublisherPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(0);
        config.setMaxIdle(10);
        config.setMaxTotal(10);
        pool = new GenericObjectPool<>(new PublisherFactory(),config);
    }

    private static PublisherPool instance = new PublisherPool();

    public static PublisherPool getInstance() {
        return instance;
    }

    public void publish(String message,String topic) throws Exception {
        Publisher publisher = pool.borrowObject();
        publisher.publish(message, topic);
        pool.returnObject(publisher);
    }
    public void publish(String exchangeName,String message,String topic) throws Exception {
        Publisher publisher = pool.borrowObject();
        publisher.publish(exchangeName,message, topic);
        pool.returnObject(publisher);
    }

}

