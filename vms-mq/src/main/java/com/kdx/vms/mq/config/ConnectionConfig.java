package com.kdx.vms.mq.config;

/**
 * mq连接配置
 * Created by zhangguodong on 2017/7/3.
 */
public class ConnectionConfig {

    public static String  HOST;
    public static Integer PORT;
    public static String  USER_NAME;
    public static String  PASS_WORD;
    public static Integer POOL_SIZE;

    public static String getHOST() {
        return HOST;
    }

    public static void setHOST(String HOST) {
        ConnectionConfig.HOST = HOST;
    }

    public static Integer getPORT() {
        return PORT;
    }

    public static void setPORT(Integer PORT) {
        ConnectionConfig.PORT = PORT;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public static void setUserName(String userName) {
        USER_NAME = userName;
    }

    public static String getPassWord() {
        return PASS_WORD;
    }

    public static void setPassWord(String passWord) {
        PASS_WORD = passWord;
    }

    public static Integer getPoolSize() {
        return POOL_SIZE;
    }

    public static void setPoolSize(Integer poolSize) {
        POOL_SIZE = poolSize;
    }

}
