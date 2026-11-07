package com.kdx.vms.commons.util;

import com.kdx.vms.commons.RedisConfig;
import com.kdx.vms.commons.RedisConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Redis 静态工具类。
 *
 * 改造说明（VMS Phase 0，2026-11）：原实现基于 JedisCluster（要求 Redis 集群模式）。
 * 单点 Redis（无 cluster 支持）下改为 JedisPool，静态方法签名保持不变，调用方零改动。
 *
 * Created by liyang on 2017/2/23.
 */
public class MyJedisUtils {

    private static Logger logger = LoggerFactory.getLogger(MyJedisUtils.class);

    private static JedisPool jedisPool;

    /*static {
        init();
    }

    private static void init() {
        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxIdle(RedisConfig.getInstance().getMaxIdle());
        jpc.setMaxTotal(RedisConfig.getInstance().getMaxActive());
        jpc.setMaxWaitMillis(RedisConfig.getInstance().getMaxWait());
        jpc.setTestOnBorrow(RedisConfig.getInstance().isTestOnBorrow());
        jpc.setTestOnReturn(RedisConfig.getInstance().isTestOnReturn());
        jedisPool = new JedisPool(jpc, RedisConfig.getInstance().getIp(), RedisConfig.getInstance().getPort(), RedisConfig.getInstance().getTimeout(), RedisConfig.getInstance().getPass());
    }*/
    public static void initial(){
        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxIdle(RedisConfiguration.getInstance().getMaxIdle());
        jpc.setMaxTotal(RedisConfiguration.getInstance().getMaxTotal());
        jpc.setMaxWaitMillis(RedisConfiguration.getInstance().getMaxWaitMillis());
        jpc.setTestOnBorrow(RedisConfiguration.getInstance().isTestOnBorrow());
        jpc.setTestOnReturn(RedisConfiguration.getInstance().isTestOnReturn());
        RedisConfiguration cfg = RedisConfiguration.getInstance();
        if (StringUtil.isEmpty(cfg.getPassWord())) {
            jedisPool = new JedisPool(jpc, cfg.getIp(), cfg.getPort(), cfg.getTimeout());
        } else {
            jedisPool = new JedisPool(jpc, cfg.getIp(), cfg.getPort(), cfg.getTimeout(), cfg.getPassWord());
        }
    }

    /** 在单个 borrowed Jedis 上执行并归还连接 */
    private interface Action<T> {
        T run(Jedis jedis);
    }

    private static <T> T exec(Action<T> action) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return action.run(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    /**                             hash方式                                    **/

    /**
     * hash方式存储
     * @param key
     * @param field
     * @param value
     * @return
     */
    public static boolean hSet(String key, String field, String value) {
        try {
            return exec(jedis -> jedis.hset(key, field, value)) >= 0;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 获取hash结构的消息
     *
     * @param key
     * @param field
     * @return
     */
    public static String hGet(String key, String field) {
        try {
            return exec(jedis -> jedis.hget(key, field));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "";
    }

    /**
     * 获取hash结构的所有消息
     *
     * @param key
     * @return
     */
    public static Map<String, String> hGetAll(String key) {
        try {
            return exec(jedis -> jedis.hgetAll(key));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * 删除hash结构的消息
     *
     * @param key
     * @param field
     * @return
     */
    public static boolean hDel(String key, String field) {
        try {
            return exec(jedis -> jedis.hdel(key, field)) >= 0;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**                       list方式                                **/

    /**
     * 获取所有消息，链表结构
     *
     * @param key
     * @return
     */
    public static List<String> getAll(String key) {
        try {
            return exec(jedis -> jedis.lrange(key, 0, -1));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取链表第一个元素
     *
     * @param key
     * @return
     */
    public static String getPop(String key) {
        try {
            List<String> result = exec(jedis -> jedis.lrange(key, 0, 0));
            return result != null && result.size() > 0 ? result.get(0) : null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 推入链表最后一个元素后面
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean push(String key, String value) {
        try {
            return exec(jedis -> jedis.rpush(key, value)) >= 0;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 从链表中移除第一个元素
     *
     * @param key
     * @return
     */
    public static boolean removeMsg(String key) {
        try {
            return exec(jedis -> jedis.lpop(key)) != null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public static boolean hDel(String msgKey, String[] fields) {
        try {
            return exec(jedis -> jedis.hdel(msgKey, fields)) >= 0;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

}
