package com.missxhh.controller;

import com.missxhh.constant.MQConst;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send/msg/low")
    public String sendMessage(){
        String sendMsg = "low priority msg, send time: " + System.currentTimeMillis();
        sendPriorityMsg(rabbitTemplate.getExchange(), MQConst.QUEUE_TEST_PRIORITY, sendMsg, 1);
        return "发送低优先级消息成功";
    }

    @GetMapping("/send/msg/high")
    public String sendHighMessage(@RequestParam Integer level){
        level = level == null ? 10 : level; // 消息优先级
        String sendMsg = "high priority msg, msg level " + level + ", send time: " + System.currentTimeMillis();
        sendPriorityMsg(rabbitTemplate.getExchange(), MQConst.QUEUE_TEST_PRIORITY, sendMsg, level);
        return "发送高优先级消息成功";
    }

    /***
     * rabbit-mq 发送优先级的消息
     * @author hjf
     * @date 2021/1/5
     **/
    private void sendPriorityMsg(String exchange, String routeKey, Object msg, Integer priority) {
        MessagePostProcessor messagePostProcessor = message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setContentEncoding("utf-8");  // 设置字符编码
            messageProperties.setPriority(priority);        // 设置消息优先级
            return message;
        };
        rabbitTemplate.convertAndSend(exchange, routeKey, msg, messagePostProcessor);
    }
}
