package com.kdx.vms.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yangshuxi on 2017/7/19.
 */
public class Publisher {
    private static Logger logger = LoggerFactory.getLogger(Publisher.class);
    private Channel channel;
    private String exchangeName;
    private static Connection connection;

    //    private static Publisher instance = new Publisher();
    //    public static Publisher getInstance() throws Exception {
//        return instance;
//    }
    private Publisher() {
        try {
            this.init();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    public static Publisher newInstance() throws Exception {
        return new Publisher();
    }

    static {
        try {
            ConnectionFactory factory = new ConnectionFactory();

            //hostname of your rabbitmq server
            factory.setHost(ConnectionConfig.getHost());

            //port of your rabbitmq server
            factory.setPort(ConnectionConfig.getPort());

            //username of your rabbitmq server
            factory.setUsername(ConnectionConfig.getUserName());

            //password of your rabbitmq server
            factory.setPassword(ConnectionConfig.getPassWord());

            // 设置自动重连
            factory.setAutomaticRecoveryEnabled(true);

            //豪秒
            factory.setNetworkRecoveryInterval(5000);

            factory.setTopologyRecoveryEnabled(false);

            //getting a connection
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {

        channel = connection.createChannel();
        exchangeName = ConnectionConfig.getExchangeName();

        //这样RabbitMQ就会使得每个Consumer在同一个时间点最多处理一个Message。换句话说，在接收到该Consumer的ack前，他它不会将新的Message分发给它。
        channel.basicQos(1);
        channel.confirmSelect();
    }

    public synchronized void publish(String message, String topic) throws Exception {
        logger.info("send message ,exchangeName =" + exchangeName + " ,topic =  " + topic + " ,message = " + message);
        channel.basicPublish(exchangeName, topic, null, message.getBytes("UTF-8"));
        boolean success = channel.waitForConfirms(2000);
        if (!success) {
            throw new RuntimeException("publish failed");
        }
    }
    public synchronized void publish(String exchangeName,String message, String topic) throws Exception {
        logger.info("send message ,exchangeName =" + exchangeName + " ,topic =  " + topic + " ,message = " + message);
        channel.basicPublish(exchangeName, topic, null, message.getBytes("UTF-8"));
        boolean success = channel.waitForConfirms(2000);
        if (!success) {
            throw new RuntimeException("publish failed");
        }
    }

    public void destroy() throws Exception {
        this.channel.close();
    }


}
