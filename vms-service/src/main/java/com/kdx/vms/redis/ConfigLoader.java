package com.kdx.vms.redis;

import com.kdx.vms.commons.McsConfiguration;
import com.kdx.vms.commons.RedisConfiguration;
import com.kdx.vms.commons.util.MyJedisUtils;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.utils.SysSettingsCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/22.
 */
public class ConfigLoader implements Runnable{
    MyJedisCluster myJedisCluster;

    public ConfigLoader(MyJedisCluster myJedisCluster) {
        this.myJedisCluster = myJedisCluster;
    }

    @Override
    public void run() {
        Map<String,String> redisMap = SysSettingsCache.getSettingsValueByParentKey(TSysSettings.REDIS_TYPE);
        Map<String,String> mcsMap = SysSettingsCache.getSettingsValueByParentKey(TSysSettings.MCS_TYPE);
        RedisConfiguration.newInstance(redisMap);
        McsConfiguration.newInstance(mcsMap);
        MyJedisUtils.initial();
        myJedisCluster.refreshConfig();
    }
}
