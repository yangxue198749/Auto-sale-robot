package com.kdx.vms.mq;


import com.kdx.vms.commons.util.PropertiesLoader;

/**
 * mq连接配置
 * Created by zhangguodong on 2017/7/3.
 */
public class ConnectionConfig {

    private static ConnectionConfig connectionConfig = new ConnectionConfig();
    private static String HOST;

    private static int PORT;

    private static String USER_NAME;

    private static String PASS_WORD;

    private static int POOL_SIZE;

    private static String exchangeName;



    static{
        try {
            connectionConfig.setHost((String) PropertiesLoader.map.get("mq_host"));
            connectionConfig.setPort(Integer.parseInt(String.valueOf(PropertiesLoader.map.get("mq_port"))));
            connectionConfig.setUserName((String) PropertiesLoader.map.get("mq_user_name"));
            connectionConfig.setPassWord((String) PropertiesLoader.map.get("mq_pass_word"));
            connectionConfig.setPoolSize(Integer.parseInt(String.valueOf(PropertiesLoader.map.get("pool_size"))));
            connectionConfig.setExchangeName((String) PropertiesLoader.map.get("responseExchangeName"));
        } catch (Exception e) {

        }
    }

    public static String getExchangeName() {
        return exchangeName;
    }

    public static void setExchangeName(String exchangeName) {
        ConnectionConfig.exchangeName = exchangeName;
    }

    public static String getHost() {
        return HOST;
    }

    public static void setHost(String host) { HOST = host;
    }

    public static int getPort() {
        return PORT;
    }

    public static void setPort(int port) {
        PORT = port;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public static void setUserName(String userName){USER_NAME = userName;
    }

    public static String getPassWord() {
        return PASS_WORD;
    }

    public static void setPassWord(String passWord) {
        PASS_WORD = passWord;
    }
    public static int getPoolSize() {
        return POOL_SIZE;
    }

    public static void setPoolSize(int poolSize) {
        POOL_SIZE = poolSize;
    }
}
