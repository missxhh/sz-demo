package com.missxhh.consumer;

import com.missxhh.constant.MQConst;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PullConsumer implements ApplicationRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 拉取消息并处理 自动ack
        // new Thread(new ReceiveMsgAutoAck()).run();
        // 拉取消息并处理 手动
        new Thread(new ReceiveMsg()).run();
    }

    class ReceiveMsgAutoAck implements Runnable{
        @Override
        public void run() {
            while(true) {
                String msg = (String) rabbitTemplate.receiveAndConvert(MQConst.QUEUE_TEST_PRIORITY);
                if(!StringUtils.isEmpty(msg)) {
                    System.out.println("receive msg：" + msg);
                    System.out.println("handle msg.....");
                    try {
                        Thread.sleep(30000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("===========msg handle finish!============");
                }
            }
        }
    }

    class ReceiveMsg implements Runnable{
        @Override
        public void run() {
            while(true) {
                rabbitTemplate.execute(new ChannelCallback() {
                    @Override
                    public Object doInRabbit(Channel channel) throws Exception {
                        GetResponse response = channel.basicGet(MQConst.QUEUE_TEST_PRIORITY, false);
                        // 获取一个消息
                        if(response != null) {
                            try {
                                System.out.println("receive msg：" + new String(response.getBody()));
                                System.out.println("handle msg.....");
                                Thread.sleep(3000L);
                                System.out.println("===========msg handle finish!============");
                                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
                            } catch(Exception e) {
                                channel.basicNack(response.getEnvelope().getDeliveryTag(), false, true);
                            }
                        }
                        return null;
                    }
                });
            }
        }
    }

}
