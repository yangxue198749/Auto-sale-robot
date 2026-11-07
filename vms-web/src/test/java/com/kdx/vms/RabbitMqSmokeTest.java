package com.kdx.vms;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Phase 0/一期闭环：RabbitMQ 端到端收发验证。
 * 用 setting.properties 中的连接参数，向 open-api 交换机发布消息，
 * 再用临时队列绑定相同 routing key 消费，验证完整链路。
 */
public class RabbitMqSmokeTest {

    @Test
    public void testPublishAndConsume() throws Exception {
        String host = (String) PropertiesLoader.map.get("mq_host");
        int port = Integer.parseInt((String) PropertiesLoader.map.get("mq_port"));
        String user = (String) PropertiesLoader.map.get("mq_user_name");
        String pass = (String) PropertiesLoader.map.get("mq_pass_word");
        String exchange = (String) PropertiesLoader.map.get("responseExchangeName");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        factory.setAutomaticRecoveryEnabled(true);

        String routingKey = "test.closure." + UUID.randomUUID();
        String payload = "vms-closure-test-" + System.currentTimeMillis();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        try {
            // 1. 声明一个临时队列并绑定到 open-api 交换机
            String queue = channel.queueDeclare().getQueue();
            channel.queueBind(queue, exchange, routingKey);

            // 2. 发布消息
            channel.basicPublish(exchange, routingKey,
                    new AMQP.BasicProperties.Builder().deliveryMode(2).build(),
                    payload.getBytes("UTF-8"));

            // 3. 主动拉取验证
            GetResponse response = channel.basicGet(queue, true);
            assertNotNull("应能从绑定队列消费到消息", response);
            String received = new String(response.getBody(), "UTF-8");
            assertEquals(payload, received);

            // 4. 验证队列声明（模拟应用消费者行为）
            Map<String, Object> args = new HashMap<String, Object>();
            channel.queueDeclare("queue.closure.smoke.v1", true, false, false, args);
            channel.queueBind("queue.closure.smoke.v1", exchange, "request.closure.smoke");
            long messageCount = channel.messageCount("queue.closure.smoke.v1");
            System.out.println("[SMOKE] MQ publish/consume OK, payload=" + payload
                    + "; durable queue declared, messageCount=" + messageCount);

            // 清理临时持久化队列
            channel.queueDelete("queue.closure.smoke.v1");
            System.out.println("[SMOKE] OK: RabbitMQ end-to-end works through open-api exchange");
        } finally {
            channel.close();
            connection.close();
        }
    }
}
