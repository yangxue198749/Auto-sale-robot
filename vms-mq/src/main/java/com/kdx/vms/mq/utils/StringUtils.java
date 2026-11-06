package com.kdx.vms.mq.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by liyang on 2017/7/12.
 */
public class StringUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

    final static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String md5(String s) {
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static String sign(Map<String, String> map, String key) {
        if (null == map || map.isEmpty() || isEmpty(key)) {
            return "";
        }
        String parameter = map.keySet().stream()
                .filter(s -> !"".equals(s))
                .filter(s -> 0 < map.get(s).trim().length())
                .filter(s -> !"sign".equalsIgnoreCase(s))
                .sorted((o1, o2) -> o1.compareTo(o2))
                .map(s -> s + "=" + map.get(s))
                .collect(Collectors.joining("&"));
        return md5(parameter + "&key=" + key);
    }

}
