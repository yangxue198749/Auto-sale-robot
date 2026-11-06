package com.kdx.vms.webapi.thread;

import com.kdx.vms.webapi.Counter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yangshuxi on 2017/1/13.
 */
public abstract class PushThread<T> implements Runnable{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String url;
    protected List<T> subList;
    protected Object name;
    CountDownLatch latch;
    Counter counter;

    PushThread(String url, List<T> subList, CountDownLatch latch, Object name,Counter counter) {
        this.url = url;
        this.subList = subList;
        this.latch = latch;
        this.name = name;
        this.counter = counter;
    }

}
