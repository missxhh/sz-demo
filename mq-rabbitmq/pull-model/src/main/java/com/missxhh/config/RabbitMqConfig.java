package com.missxhh.config;

import com.missxhh.constant.MQConst;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.host}")
    private String addresses;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirms}")
    private boolean publisherConfirms;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses+":"+port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        // 如果要进行消息回调，则这里必须要设置为true
        connectionFactory.setPublisherConfirms(publisherConfirms);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate newRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        // 失败通知
        template.setMandatory(true);
        // 失败回调
        template.setReturnCallback(returnCallback());
        return template;
    }

    /***
     * 声明队列（优先队列）
     * @author hjf
     * @date 2021/1/5
     **/
    @Bean
    public Queue queueUserMessages() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-max-priority", 10);
        return new Queue(MQConst.QUEUE_TEST_PRIORITY, true, false, false, map);
    }


    /**
     * 交换器
     * @author hjf
     * @date 2021/1/5
     **/
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(MQConst.EXCHANGE_TOPIC);
    }

    /***
     * 失败回调
     * @author hjf
     * @date 2021/1/5
     **/
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback(){
        return new RabbitTemplate.ReturnCallback(){
            @Override
            public void returnedMessage(Message message,
                                        int replyCode,
                                        String replyText,
                                        String exchange,
                                        String routingKey) {
                System.out.println("msg send error, Returned replyText：" + replyText + ", Returned exchange："
                        + exchange + ", Returned routingKey：" + routingKey + ", Returned Message：" + new String(message.getBody()));
            }
        };
    }
}
