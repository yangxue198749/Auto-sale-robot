package com.kdx.vms.commons.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yangshuxi on 2016/12/16.
 */
public class PropertiesLoader {
	
	public static volatile Map<Object, Object> map;
	
	static{
		try {
			Properties props=new Properties();
			Method method = null;
            method = Thread.class.getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader loader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
            URL in = loader.getResource("setting.properties");
            props.load(in.openStream());
            // JVM 系统属性（-D）优先于配置文件：部署环境通过 setenv 覆盖
            // 存储根目录等与机器相关的配置（如 -Dlocal.storage.root=/data/vms-data）
            props.putAll(System.getProperties());
			map = props;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * @Description:根据key读取value
     * @param :file 文件路径
     * @param :key 对应的key值
     * @return value值
     * @throws Exception
     */
    public static String readPropertiesValue(String file, String key) {
        try {
            Properties pro = new Properties();
            Method method = null;
            method = Thread.class.getMethod("getContextClassLoader", (Class<?>[]) null);
            ClassLoader loader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
            URL in = loader.getResource(file);
            pro.load(in.openStream());
            String value = pro.getProperty(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
