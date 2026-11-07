package com.kdx.vms;

import com.kdx.vms.commons.RedisConfiguration;
import com.kdx.vms.commons.util.MyJedisUtils;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.utils.SysSettingsCache;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Phase 0 冒烟测试：验证改造后的 Redis 包装类（JedisPool 单点模式）能正常读写。
 * 覆盖两个包装：MyJedisUtils（静态工具）与 MyJedisCluster（Spring Bean 包装）。
 */
public class RedisConnectionSmokeTest {

    @Test
    public void testRedisWrappersWorkOnSingleNode() throws Exception {
        // 1. 从 T_SYS_SETTINGS 加载配置并初始化静态工具
        Map<String, String> redisMap =
                SysSettingsCache.getSettingsValueByParentKey(TSysSettings.REDIS_TYPE);
        RedisConfiguration.newInstance(redisMap);
        RedisConfiguration cfg = RedisConfiguration.getInstance();
        MyJedisUtils.initial();

        System.out.println("[SMOKE] redis " + cfg.getIp() + ":" + cfg.getPort()
                + " password=[" + (cfg.getPassWord() == null || cfg.getPassWord().isEmpty() ? "none" : "set") + "]");

        // 2. 验证 MyJedisUtils（hash 读写）
        boolean ok = MyJedisUtils.hSet("vms:smoke:hash", "field1", "hello-utils");
        assertTrue("MyJedisUtils.hSet 应返回 true", ok);
        assertEquals("hello-utils", MyJedisUtils.hGet("vms:smoke:hash", "field1"));

        // 3. 验证 MyJedisCluster（通过反射调用私有 init 建立 JedisPool）
        MyJedisCluster cluster = new MyJedisCluster();
        Method init = MyJedisCluster.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(cluster);

        cluster.hset("vms:smoke:cluster", "field1", "hello-cluster");
        assertEquals("hello-cluster", cluster.hget("vms:smoke:cluster", "field1"));
        Long len = cluster.llen("vms:smoke:list");
        System.out.println("[SMOKE] llen(empty list) = " + len);

        // 4. 验证过期设置（setPexpireKey，促销事件核心机制）
        Boolean set = cluster.setPexpireKey("vms:smoke:expire", "x", 60);
        assertTrue("setPexpireKey 应返回 true", set);
        Long ttl = cluster.expire("vms:smoke:expire", 30);
        System.out.println("[SMOKE] expire result = " + ttl);

        // 清理
        cluster.delKey("vms:smoke:hash");
        cluster.delKey("vms:smoke:cluster");
        cluster.delKey("vms:smoke:expire");

        System.out.println("[SMOKE] OK: both Redis wrappers work on single-node Redis via JedisPool");
    }
}
