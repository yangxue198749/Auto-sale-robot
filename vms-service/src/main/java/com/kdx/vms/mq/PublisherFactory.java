package com.kdx.vms.mq;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by yangshuxi on 2017/7/20.
 */
public class PublisherFactory implements PooledObjectFactory<Publisher> {

    @Override
    public PooledObject<Publisher> makeObject() throws Exception {
        return new DefaultPooledObject<>(Publisher.newInstance());
    }

    @Override
    public void destroyObject(PooledObject<Publisher> pooledObject) throws Exception {
        pooledObject.getObject().destroy();
    }

    @Override
    public boolean validateObject(PooledObject<Publisher> pooledObject) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<Publisher> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<Publisher> pooledObject) throws Exception {

    }
}

