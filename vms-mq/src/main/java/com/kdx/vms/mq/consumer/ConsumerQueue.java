package com.kdx.vms.mq.consumer;

import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.mq.utils.StringUtils;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 * Created by zhangguodong on 2017/7/3.
 */
public class ConsumerQueue implements Runnable, Consumer{

    Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ServiceHandler handler;
    private String responseTopic;
    private String queueName;
    private String responseExchangeName;
    private String requestTopic;
    private Channel channel;

    /**
     * 设置由谁来处理消息
     * @param handler
     */
    protected void setHandler(ServiceHandler handler) {
        this.handler = handler;
    }

    /**
     *
     * @param channel
     * @param queueName 从这个队列接收消息
     * @param responseTopic 处理结果返回到这个主题
     * @param responseExchangeName 处理结果返回到这个交换机
     * @throws Exception
     */
    protected ConsumerQueue(Channel channel,String queueName, String requestTopic, String responseTopic, String responseExchangeName) throws IOException,TimeoutException,NullPointerException{

         if(null == queueName || "".equals(queueName)){
             throw new NullPointerException(" queueName can't be null ! ");
         }
         if(null == requestTopic || "".equals(requestTopic)){
             throw new NullPointerException(" requestTopic can't be null ! ");
         }
         if(null == responseTopic || "".equals(responseTopic)){
             throw new NullPointerException(" responseTopic can't be null ! ");
         }
         if(null == responseExchangeName || "".equals(responseExchangeName)){
             throw new NullPointerException(" responseExchangeName can't be null ! ");
         }

         this.responseTopic = responseTopic;
         this.requestTopic = requestTopic;
         this.queueName = queueName;
         this.responseExchangeName = responseExchangeName;
         this.channel = channel;

    }

    protected ConsumerQueue(Channel channel,String queueName, String requestTopic, String requestExchangeName) throws IOException,TimeoutException,NullPointerException{

        if(null == queueName || "".equals(queueName)){
            throw new NullPointerException(" queueName can't be null ! ");
        }
        if(null == requestTopic || "".equals(requestTopic)){
            throw new NullPointerException(" requestTopic can't be null ! ");
        }
        if(null == requestExchangeName || "".equals(requestExchangeName)){
            throw new NullPointerException(" requestExchangeName can't be null ! ");
        }
        this.requestTopic = requestTopic;
        this.queueName = queueName;
        this.responseExchangeName = requestExchangeName;
        this.channel = channel;

    }
    public void run() {
        try {
            if(null == handler){
                throw new NullPointerException(" handler can't be null ! ");
            }

            channel.queueDeclare(queueName, // 队列名称
                    true, // 是否持久化（true表示是，队列将在服务器重启时生存）
                    false, // 是否是独占队列（创建者可以使用的私有队列，断开后自动删除）
                    false, // 当所有消费者客户端连接断开时是否自动删除队列
                    null); // 其他参数
            channel.queueBind(queueName, responseExchangeName, requestTopic); // 可以直接对接另一个队列
            /**
             * 设置从该队列里面取消息并手动应答
             */
            channel.basicConsume(queueName, false,this);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**
     * Called when consumer is registered.
     */
    public void handleConsumeOk(String consumerTag) {
        LOGGER.info("Consumer "+consumerTag +" registered");
    }

    /**
     * Called when new message is available.
     */

    public void handleDelivery(String consumerTag, Envelope env,
                               BasicProperties props, byte[] body) throws IOException{
        String  message = new String(body,"utf-8");
        LOGGER.info(" Receive message : " + message+ " ");
        try {
            HandleResponse result = handler.deal(message);
            //将处理结果发送到指定的  交换机和主题
            String responseExchangeName = this.responseExchangeName;
            String responseTopic = this.responseTopic;
            if(!StringUtils.isEmpty(result.getRespExchange()))responseExchangeName= result.getRespExchange();
            if(!StringUtils.isEmpty(result.getRespTopic()))responseTopic= result.getRespTopic();
            if(!StringUtils.isEmpty(responseTopic) && !StringUtils.isEmpty(result.getResult())){
                //如果响应主题和相应内同都不为空,发送消息
                sendMessage(responseExchangeName,responseTopic,result.getResult());
            }
            channel.basicAck(env.getDeliveryTag(),false);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(),e);
            throw new IOException(e);
        }
    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}

    /**将消息的处理结果发送到指定的交换机主题**/
    protected void sendMessage(String exchangeName,String topic ,String message) {
        LOGGER.info("send message ,exchangeName =" + exchangeName + " ,topic =  " + topic + " ,message = " + message);
        try {
            channel.basicPublish(exchangeName,topic,null,message.getBytes("UTF-8"));
        }catch (IOException e){
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
}
