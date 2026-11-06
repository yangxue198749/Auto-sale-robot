package com.kdx.mp.commons.util;

import com.kdx.vms.commons.util.PropertiesLoader;

/**
 * Created by yangshuxi on 2017/4/5.
 */
public class WeChatConfig {
    private static String SUFFIX = PropertiesLoader.readPropertiesValue("setting.properties","weChat_suffix");
    public static String APP_ID = SUFFIX.equalsIgnoreCase("com") ? "wxd4475a6e9d648614" : "wxa6a2e53865288897";
    public static String SECRET = SUFFIX.equalsIgnoreCase("com") ? "681213adf19b8c2f511ab3b0f6e744d4" : "c26615b679f6cce9fc64365d0a5ed4ec";
    public static String TEMPLATE_ID = SUFFIX.equalsIgnoreCase("com") ? "qNLzkEPWR7M7dWoLjXWELYpG_MkO2FcL0b7BsBzCks8" : "au6MZizx1RC6N5oY7ubS8X52Zwuheb0VYEkKYUK5G2I";
    public static String MSG_URL = SUFFIX.equalsIgnoreCase("com") ? "http://vms.touchfound.com" : "http://vms.touchfound.net";
}
