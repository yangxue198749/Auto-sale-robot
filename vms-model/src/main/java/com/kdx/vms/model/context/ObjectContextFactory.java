package com.kdx.vms.model.context;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.Transaction;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by liyang on 2016/11/25.
 */
public class ObjectContextFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ObjectContextFactory instance = new ObjectContextFactory();

    private ServerRuntime cayenneRuntime;

    private ObjectContextFactory() {
        this.cayenneRuntime = new ServerRuntime("cayenne-vms.xml");
    }

    public static ObjectContextFactory getInstance() {
        return instance;
    }

    public ObjectContext newObjectContext() {
        return this.cayenneRuntime.getContext();
    }

    public ObjectContext getChildObjectContext(ObjectContext context) {
        return this.cayenneRuntime.getContext((DataChannel) context);
    }

    public Transaction getTransaction() {
        return this.cayenneRuntime.getDataDomain().createTransaction();
    }

}
