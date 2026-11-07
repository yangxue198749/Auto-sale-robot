package com.kdx.vms.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5Util {

    private final static Logger LOGGER = LoggerFactory.getLogger(Md5Util.class);

	final static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

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

    public static String md5ByInputStream(InputStream in) {
        String value = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // 改造：原依赖 aliyun-sdk 的 IOUtils，改用 JDK 原生流读取（OSS 停用后移除外部依赖）
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while ((len = in.read(chunk)) != -1) {
                buffer.write(chunk, 0, len);
            }
            md5.update(buffer.toByteArray());
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
            while (value.length() < 32) {
                value = "0" + value;
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return value;
    }

}
