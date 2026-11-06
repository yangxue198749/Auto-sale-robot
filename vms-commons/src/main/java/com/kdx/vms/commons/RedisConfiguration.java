package com.kdx.vms.commons;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/22.
 */
public class RedisConfiguration {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String KEY_IP                   = "redis.ip";
    private final static String KEY_PORT                 = "redis.port";
    private final static String KEY_TIMEOUT              = "redis.timeout";
    private final static String KEY_MAX_REDIRECTIONS     = "redis.maxRedirections";
    private final static String KEY_PASS_WORD            = "redis.passWord";

    private final static String KEY_MAX_IDLE             = "redis.maxIdle";
    private final static String KEY_MIN_IDLE             = "redis.minIdle";
    private final static String KEY_MAX_TOTAL            = "redis.maxTotal";
    private final static String KEY_MAX_WAIT_MILLIS      = "redis.maxWaitMillis";
    private final static String KEY_TEST_ON_BORROW       = "redis.testOnBorrow";
    private final static String KEY_TEST_ON_RETURN       = "redis.testOnReturn";


    private String  ip;
    private int     port;
    private int     timeout;
    private int     maxRedirections;
    private String  passWord;

    private int     maxIdle;
    private int     minIdle;
    private int     maxTotal;
    private int     maxWaitMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;


    public void load(Map<String,String> map){
        try {
            ip              = StringUtil.getStringValue(map.get(KEY_IP),"192.168.101.234");
            port            = StringUtil.getIntValue(map.get(KEY_PORT),6379);
            timeout         = StringUtil.getIntValue(map.get(KEY_TIMEOUT),5000);
            maxRedirections = StringUtil.getIntValue(map.get(KEY_MAX_REDIRECTIONS),10);
            passWord        = StringUtil.getStringValue(map.get(KEY_PASS_WORD),"redis123");

            maxIdle         = StringUtil.getIntValue(map.get(KEY_MAX_IDLE),100);
            minIdle         = StringUtil.getIntValue(map.get(KEY_MIN_IDLE),0);
            maxTotal        = StringUtil.getIntValue(map.get(KEY_MAX_TOTAL),1024);
            maxWaitMillis   = StringUtil.getIntValue(map.get(KEY_MAX_WAIT_MILLIS),1000);
            testOnBorrow    = StringUtil.getBooleanValue(map.get(KEY_TEST_ON_BORROW),true);
            testOnReturn    = StringUtil.getBooleanValue(map.get(KEY_TEST_ON_RETURN),true);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
    private static RedisConfiguration instance = new RedisConfiguration();
    public static RedisConfiguration getInstance(){
        return instance;
    }

    public static void newInstance(Map<String,String> map){
        instance.load(map);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public String getPassWord() {
        return passWord;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }
}
