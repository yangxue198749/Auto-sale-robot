package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 * Created by yangshuxi on 2017/5/23.
 */
public class AddSettings {
    static TSysSettings root = SysSettingsCache.getRoot();
    static ObjectContext context = root.getObjectContext();
    public static void main(String[] args) {
        TSysSettings redis = add(TSysSettings.REDIS_TYPE,null,"redis相关配置",root);

        add("redis.ip","192.168.101.234","ip",redis);
        add("redis.port","6379","端口",redis);
        add("redis.timeout","5000","每个节点最大连接超时时间, 默认1000秒",redis);
        add("redis.maxRedirections","10","",redis);
        add("redis.passWord","redis123","密码",redis);
        add("redis.maxIdle","100","最大空闲连接数, 默认8个",redis);
        add("redis.minIdle","0","最小空闲连接数, 默认0",redis);
        add("redis.maxTotal","1024","最大连接数, 默认8个",redis);
        add("redis.maxWaitMillis","1000","获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间, 默认-1 ",redis);
        add("redis.testOnBorrow","true","在获取连接的时候检查有效性, 默认false",redis);
        add("redis.testOnReturn","true","在返回连接给pool的时候，检查有效性, 默认false",redis);

        TSysSettings refund = add(TSysSettings.REFUND_TYPE,null,"退款相关配置",SysSettingsCache.getRoot());
        add(TSysSettings.REFUND_MAX_MONEY,"5000","自动退款最大金额，单位为分",refund);

        TSysSettings upload = add(TSysSettings.UPLOAD_TYPE,null,"上传文件相关配置",SysSettingsCache.getRoot());
        add(TSysSettings.THEME_MAX_ZIP_SIZE,"10","主题zip包上传大小限制，单位为MB",upload);
        add(TSysSettings.THEME_MAX_PREVIEW_SIZE,"3","主题预览图上传大小限制，单位为MB",upload);
        add(TSysSettings.SKU_MAX_IMAGE_SIZE,"200","商品图片上传大小限制，单位为KB",upload);
    }
    public static TSysSettings add(String key,String value,String name,TSysSettings parent){
        TSysSettings sysSettings = context.newObject(TSysSettings.class);
        sysSettings.setParent(parent);
        sysSettings.setCtime(new Date());
        sysSettings.setCtor(UserUtils.getVmsSystem().getId());
        sysSettings.setName(name);
        sysSettings.setKey(key);
        sysSettings.setValue(value);
        context.commitChanges();
        return sysSettings;
    }
}
