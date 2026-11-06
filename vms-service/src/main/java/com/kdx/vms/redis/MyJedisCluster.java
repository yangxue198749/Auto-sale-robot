package com.kdx.vms.redis;

import com.kdx.vms.commons.McsConfiguration;
import com.kdx.vms.commons.RedisConfiguration;
import com.kdx.vms.commons.util.CacheKeysUtil;
import com.kdx.vms.commons.util.MyJedisUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.utils.SysSettingsCache;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangguodong
 */
public class MyJedisCluster {

    private static ExecutorService ES = Executors.newFixedThreadPool(100);
    protected static JedisCluster JC;
    //	private RedisConfig redisConfig;
    private RedisConfiguration redisConfig;
    private static Logger log = LoggerFactory.getLogger(MyJedisCluster.class);
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    @Resource
    private IMcsService mcsService;

    private void init() throws Exception {
        log.info("init redis client...");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        try {

            Map<String, String> redisMap = SysSettingsCache.getSettingsValueByParentKey(TSysSettings.REDIS_TYPE);
            Map<String, String> mcsMap = SysSettingsCache.getSettingsValueByParentKey(TSysSettings.MCS_TYPE);
            RedisConfiguration.newInstance(redisMap);
            McsConfiguration.newInstance(mcsMap);
            redisConfig = RedisConfiguration.getInstance();
            MyJedisUtils.initial();
            if (this.redisConfig == null)
                throw new Exception("Can not connect redis instance, must params : redisConfig");

            GenericObjectPoolConfig poolConfig = initPoolConfig();
            JC = new JedisCluster(new HostAndPort(redisConfig.getIp(), redisConfig.getPort()),
                    redisConfig.getTimeout(), redisConfig.getTimeout(), redisConfig.getMaxRedirections(),
                    redisConfig.getPassWord(), poolConfig);
            dealExpire();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void refreshConfig() {
        log.info("refresh redis config...");
        redisConfig = RedisConfiguration.getInstance();
        if (this.redisConfig != null) {
            GenericObjectPoolConfig poolConfig = initPoolConfig();
            JC = new JedisCluster(new HostAndPort(redisConfig.getIp(), redisConfig.getPort()),
                    redisConfig.getTimeout(), redisConfig.getTimeout(), redisConfig.getMaxRedirections(),
                    redisConfig.getPassWord(), poolConfig);
        }
    }


    /**
     * 处理过期通知事件
     */
    private void dealExpire() {
        this.subscribe(new SubscribeInterface() {
            @Override
            public void execute(String arg0, String arg1, String message) {
                log.debug("收到过期通知" + arg0 + arg1 + message);
                log.debug("收到过期通知，过期的key = " + message);
                if (!StringUtil.isEmpty(message)) {
                    if (message.startsWith(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK)) {
                        //代表过期的是促销策略
                        //重新下发促销
                        String[] demp = message.split(":");
                        if (demp.length == 3) {
                            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, Integer.parseInt(demp[1]));
                            if (mtVm != null) {
                                String errorMsg = mcsService.sendSkuPriceToMcs(context, mtVm, true);
                                if (!StringUtil.isEmpty(errorMsg)) {
//							//代表着失败，失败之后重新存到redis中，一分钟后重发
                                    log.error("收到过期通知,同步商品价格失败，失败原因：" + errorMsg);
                                    setPexpireKey(message, message, 60);
                                }
                            } else {
                                log.error("收到过期价格通知,处理失败,售货机不存在 vmId = " + demp[1]);
                            }
                        }
                    }
                }
            }
        }, "__keyevent@0__:expired");
    }

    private GenericObjectPoolConfig initPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
//        poolConfig.setBlockWhenExhausted(this.redisConfig.isBlockWhenExhausted());
//        poolConfig.setLifo(this.redisConfig.isLifo());
        poolConfig.setMaxIdle(this.redisConfig.getMaxIdle());
        poolConfig.setMaxTotal(this.redisConfig.getMaxTotal());
        poolConfig.setMaxWaitMillis(this.redisConfig.getMaxWaitMillis());
//        poolConfig.setMinEvictableIdleTimeMillis(this.redisConfig.getMinEvictableIdleTimeMillis());
        poolConfig.setMinIdle(this.redisConfig.getMinIdle());
//        poolConfig.setNumTestsPerEvictionRun(this.redisConfig.getNumTestsPerEvictionRun());
//        poolConfig.setSoftMinEvictableIdleTimeMillis(this.redisConfig.getSoftMinEvictableIdleTimeMillis());
        poolConfig.setTestOnBorrow(this.redisConfig.isTestOnBorrow());
        poolConfig.setTestOnReturn(this.redisConfig.isTestOnReturn());
//        poolConfig.setTestWhileIdle(this.redisConfig.isTestWhileIdle());
//        poolConfig.setTimeBetweenEvictionRunsMillis(this.redisConfig.getTimeBetweenEvictionRunsMillis());
        return poolConfig;
    }

//    public RedisConfig getRedisConfig() {
//        return redisConfig;
//    }

//    public void setRedisConfig(RedisConfig redisConfig) {
//        this.redisConfig = redisConfig;
//    }

    /**
     * 订阅
     *
     * @param patterns 订阅号，支持正则
     * @return void
     * @date 2016年11月8日 下午5:46:59
     * @author chenifan
     * @QQ 995998760
     */
    public List<MyJedisPubSub> subscribe(SubscribeInterface si, String... patterns) {
        List<MyJedisPubSub> list = new ArrayList<MyJedisCluster.MyJedisPubSub>();
        for (Map.Entry<String, JedisPool> entry : JC.getClusterNodes().entrySet()) {
            Jedis jedis = null;
            try {
                jedis = entry.getValue().getResource();

                if (jedis == null) {
                    continue;
                }

                String result = jedis.ping();

                if (result.equalsIgnoreCase("pong")) {
                    MyJedisPubSub myJedisPubSub = new MyJedisPubSub(si, jedis, patterns);
                    ES.execute(myJedisPubSub);
                    list.add(myJedisPubSub);
                }
//		        jedis.close();
            } catch (JedisException ex) {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return list;
    }

    /**
     * 取消订阅
     *
     * @param patterns 订阅号，支持正则
     * @return void
     * @date 2016年11月8日 下午5:47:15
     * @author chenifan
     * @QQ 995998760
     */
    public void unsubscribe(List<MyJedisPubSub> list, String... patterns) {
        if (list == null) {
            return;
        }
        for (MyJedisPubSub myJedisPubSub : list) {
            myJedisPubSub.punsubscribe(patterns);
        }
    }

    public Long rpush(final String key, final String... value) {
        return JC.rpush(key, value);
    }

    public List<String> lrange(final String key, final long start, final long end) {
        return JC.lrange(key, start, end);
    }

    public String ltrim(final String key, final long start, final long end) {
        return JC.ltrim(key, start, end);
    }

    public Long hset(final String key, final String field, final String value) {
        return JC.hset(key, field, value);
    }

    public String hget(final String key, final String field) {
        return JC.hget(key, field);
    }

    public Map<String, String> hgetAll(final String key) {
        return JC.hgetAll(key);
    }

    public Long hdel(final String key, final String... field) {
        return JC.hdel(key, field);
    }

    /**
     * @param seconds 单位为秒
     *                {@link #expire(String, int)}
     */
    public Long expire(final String key, final int seconds) {
        return JC.expire(key, seconds);
    }

    /**
     * 设置 key 的过期时间亿以毫秒计（key要已存在）
     *
     * @param key
     * @param milliseconds
     * @return Long
     * @date 2016年11月15日 下午4:51:33
     * @author chenifan
     * @QQ 995998760
     */
    public Long pexpire(final String key, final int milliseconds) {
        return JC.pexpire(key, milliseconds);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)。
     *
     * @param key
     * @param value
     * @param seconds 单位为秒
     * @return Boolean
     * @date 2016年11月15日 下午4:50:33
     * @author chenifan
     * @QQ 995998760
     */
    public Boolean setPexpireKey(final String key, final String value, final int seconds) {
        return JC.setex(key, seconds, value).equals("OK");
    }


    public class MyJedisPubSub extends JedisPubSub implements Runnable {

        //回调接口
        private SubscribeInterface si;
        private String[] patterns;
        private Jedis jedis;

        public MyJedisPubSub(SubscribeInterface si, Jedis jedis, String... patterns) {
            this.si = si;
            this.jedis = jedis;
            this.patterns = patterns;
        }

        @Override
        public void run() {
            jedis.psubscribe(this, this.patterns);
        }

        // 初始化按表达式的方式订阅时候的处理
        @Override
        public void onPSubscribe(String pattern, int subscribedChannels) {
            log.info("host:" + jedis.getClient().getHost() + " port:" + jedis.getClient().getPort() + "订阅成功，订阅号为：" + pattern);
        }

        // 取得按表达式的方式订阅的消息后的处理
        @Override
        public void onPMessage(String pattern, String channel, String message) {
            log.debug("host:" + jedis.getClient().getHost() + " port:" + jedis.getClient().getPort() + "收到过期消息");
            if (si != null) {
                si.execute(pattern, channel, message);
            }
        }

        @Override
        public void punsubscribe(String... patterns) {
            log.info("host:" + jedis.getClient().getHost() + " port:" + jedis.getClient().getPort() + "取消订阅：" + Arrays.asList(patterns));
            super.punsubscribe(patterns);
        }

        public Jedis getJedis() {
            return jedis;
        }

        public void setJedis(Jedis jedis) {
            this.jedis = jedis;
        }

    }

    public interface SubscribeInterface {
        /**
         * 回调方法
         *
         * @param pattern 订阅号
         * @param channel 消息类型
         * @param message 消息
         * @return void
         * @date 2016年11月9日 下午1:06:07
         * @author chenifan
         * @QQ 995998760
         */
        public void execute(String pattern, String channel, String message);
    }

    public Long delKey(String key) {
        return JC.del(key);
    }

    public Long llen(String key) {
        return JC.llen(key);
    }
}
