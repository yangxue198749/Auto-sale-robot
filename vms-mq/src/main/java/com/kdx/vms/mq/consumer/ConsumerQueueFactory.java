package com.kdx.vms.mq.consumer;

import com.kdx.vms.mq.config.ConnectionConfig;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费者工厂
 * Created by zhangguodong on 2017/7/6.
 */
public class ConsumerQueueFactory {
    /**
     * 消息处理
     */
    private ServiceHandler handler;

    public ConsumerQueueFactory(ServiceHandler handler) {
        if (Objects.isNull(handler)) throw new IllegalArgumentException("ServiceHandler can't be null.");
        this.handler = handler;
    }

    /**
     * 创建消费者（不用返回消息）
     * @param queueName
     * @param requestTopic
     * @param requestExchangeName
     * @throws Exception
     */
    public void createConsumer(String queueName,String requestTopic,String requestExchangeName)throws Exception{

        Channel channel = getChannel();
        startExecutor(channel,queueName,requestTopic,requestExchangeName);

    }
    public void createConsumer(String queueName, String requestTopic, String responseTopic, String responseExchangeName) throws Exception {

        Channel channel = getChannel();

        startExecutor(channel,queueName,requestTopic,responseTopic,responseExchangeName);
    }

    private Channel getChannel() throws Exception {
        if (Objects.isNull(ConnectionConfig.HOST)
                || Objects.isNull(ConnectionConfig.PORT)
                || Objects.isNull(ConnectionConfig.USER_NAME)
                || Objects.isNull(ConnectionConfig.PASS_WORD)
                || Objects.isNull(ConnectionConfig.POOL_SIZE))
            throw new IllegalStateException("No set ConnectionConfig.");
        ConnectionFactory factory = new ConnectionFactory();
        //hostname of your rabbitmq server
        factory.setHost(ConnectionConfig.HOST);
        //port of your rabbitmq server
        factory.setPort(ConnectionConfig.PORT);
        //username of your rabbitmq server
        factory.setUsername(ConnectionConfig.USER_NAME);
        //password of your rabbitmq server
        factory.setPassword(ConnectionConfig.PASS_WORD);
        // 设置自动重连
        factory.setAutomaticRecoveryEnabled(true);
        //豪秒
        factory.setNetworkRecoveryInterval(5000);
        factory.setTopologyRecoveryEnabled(false);
        //getting a connection
        Connection connection = factory.newConnection();
        //creating a channel
        Channel channel = connection.createChannel();
        //这样RabbitMQ就会使得每个Consumer在同一个时间点最多处理一个Message。换句话说，在接收到该Consumer的ack前，他它不会将新的Message分发给它。
        channel.basicQos(1);
        return channel;
    }

    /**
     *
     * @param queueName 队列名字(消费者从这个队列里面取消息)
     * @param responseTopic 处理结果返回到该主题
     * @param responseExchangeName 处理结果返回到该交换机
     * @throws Exception
     */
    private void startExecutor(Channel channel,String queueName,String requestTopic,String responseTopic,String responseExchangeName) throws Exception{
        //产生一个 ExecutorService 对象，这个对象带有一个大小为 poolSize 的线程池，若任务数量大于 poolSize ，任务会被放在一个 queue 里顺序执行。
        ExecutorService executor = Executors.newFixedThreadPool(ConnectionConfig.POOL_SIZE);
        for(int i = 0; i < ConnectionConfig.POOL_SIZE; i++){
            ConsumerQueue consumerQueue = new ConsumerQueue(channel, queueName, requestTopic, responseTopic, responseExchangeName);
            consumerQueue.setHandler(handler);
            executor.execute(consumerQueue);
        }
    }

    /**
     *
     * @param channel
     * @param queueName
     * @param requestTopic
     * @param requestExchangeName
     * @throws Exception
     */
    private void startExecutor(Channel channel,String queueName,String requestTopic,String requestExchangeName) throws Exception{
        //产生一个 ExecutorService 对象，这个对象带有一个大小为 poolSize 的线程池，若任务数量大于 poolSize ，任务会被放在一个 queue 里顺序执行。
        ExecutorService executor  = Executors.newFixedThreadPool(Integer.parseInt(String.valueOf(ConnectionConfig.POOL_SIZE)));
        for(int i =0; i < ConnectionConfig.POOL_SIZE; i++){
            ConsumerQueue consumerQueue = new ConsumerQueue(channel,queueName,requestTopic,requestExchangeName);
            consumerQueue.setHandler(handler);
            executor.execute(consumerQueue);
        }
    }

}
