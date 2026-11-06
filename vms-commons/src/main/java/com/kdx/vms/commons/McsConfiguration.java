package com.kdx.vms.commons;

import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/24.
 */
public class McsConfiguration {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String KEY_MCS_IP               = "mcsIp";
    private final static String KEY_MCS_URL              = "mcsUrl";
    private final static String KEY_MCS_CONNECT_URL      = "mcsConnectUrl";
    private final static String KEY_MCS_CALLBACK_URL     = "mcsCallBackUrl";


    private String  ip;
    private String  url;
    private String  connectUrl;
    private String  callBackUrl;

    public void load(Map<String,String> map){
        try {
            ip              = StringUtil.getStringValue(map.get(KEY_MCS_IP),"192.168.101.235;172.[1][6-9]{1}.*|172.[2][0-9]{1}.*|172.[3][0-2]{1}.*");
            url             = StringUtil.getStringValue(map.get(KEY_MCS_URL),"http://192.168.101.231:8011/mcs/enter");
            connectUrl      = StringUtil.getStringValue(map.get(KEY_MCS_CONNECT_URL),"http://192.168.101.231:8011/mcs/connect");
            callBackUrl     = StringUtil.getStringValue(map.get(KEY_MCS_CALLBACK_URL),"http://192.168.101.235:18081/api/pushMessage");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private static McsConfiguration instance = new McsConfiguration();
    public static void newInstance(Map<String,String> map){
        instance.load(map);
    }


    public String getIp() {
        return ip;
    }

    public String getUrl() {
        return url;
    }

    public String getConnectUrl() {
        return connectUrl;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public static McsConfiguration getInstance() {
        return instance;
    }
}
