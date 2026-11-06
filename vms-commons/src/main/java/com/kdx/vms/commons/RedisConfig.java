package com.kdx.vms.commons;

import com.kdx.vms.commons.util.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by liyang on 2017/2/23.
 */
public class RedisConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static RedisConfig instance = new RedisConfig();

    private final static String KEY_IP              = "redis.ip";
    private final static String KEY_PORT            = "redis.port";
    private final static String KEY_PASS            = "redis.pass";
    private final static String KEY_MAXIDLE         = "redis.maxIdle";
    private final static String KEY_MAXACTIVE       = "redis.maxActive";
    private final static String KEY_MAXWAIT         = "redis.maxWait";
    private final static String KEY_TESTONBORROW    = "redis.testOnBorrow";
    private final static String KEY_TESTONRETURN    = "redis.testOnReturn";
    private final static String KEY_TIMEOUT         = "redis.timeout";
    private final static String KEY_MAXREDIRECTIONS = "redis.maxRedirections";

    private String  ip;
    private int     port;
    private String  pass;
    private int     maxIdle;
    private int     maxActive;
    private int     maxWait;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private int     timeout;
    private int     maxRedirections;

    private RedisConfig() {
        load();
    }

    public static RedisConfig getInstance() {
        return instance;
    }

    public void load() {
        try {
            ip              = PropertiesLoader.map.get(KEY_IP).toString();
            port            = Integer.parseInt(PropertiesLoader.map.get(KEY_PORT).toString());
            pass            = PropertiesLoader.map.get(KEY_PASS).toString();
            maxIdle         = Integer.parseInt(PropertiesLoader.map.get(KEY_MAXIDLE).toString());
            maxActive       = Integer.parseInt(PropertiesLoader.map.get(KEY_MAXACTIVE).toString());
            maxWait         = Integer.parseInt(PropertiesLoader.map.get(KEY_MAXWAIT).toString());
            testOnBorrow    = Boolean.parseBoolean(PropertiesLoader.map.get(KEY_TESTONBORROW).toString());
            testOnReturn    = Boolean.parseBoolean(PropertiesLoader.map.get(KEY_TESTONRETURN).toString());
            timeout         = Integer.parseInt(PropertiesLoader.map.get(KEY_TIMEOUT).toString());
            maxRedirections = Integer.parseInt(PropertiesLoader.map.get(KEY_MAXREDIRECTIONS).toString());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getPass() {
        return pass;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public int getTimeout() {
        return timeout;
    }

    public static void main(String[] args) {
        System.out.println(getInstance().getIp());
    }

}
