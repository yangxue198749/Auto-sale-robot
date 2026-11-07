package com.kdx.vms;

import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.utils.SysSettingsCache;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Phase 0 冒烟测试：验证 T_SYS_SETTINGS 设置树能被 SysSettingsCache 正确读取。
 * 这是 MyJedisCluster 初始化（Redis 连接参数来源）的前置依赖。
 */
public class SettingsSeedSmokeTest {

    @Test
    public void testSettingsTreeLoaded() {
        Map<String, String> redisSettings =
                SysSettingsCache.getSettingsValueByParentKey(TSysSettings.REDIS_TYPE);

        System.out.println("[SMOKE] redis settings = " + redisSettings);
        assertNotNull("redis 配置分组不应为空", redisSettings);
        assertTrue("应包含 redis.ip", redisSettings.containsKey("redis.ip"));
        assertTrue("redis.ip 应指向本地", "127.0.0.1".equals(redisSettings.get("redis.ip")));
        System.out.println("[SMOKE] OK: settings tree loaded, redis.ip=" + redisSettings.get("redis.ip"));
    }
}
