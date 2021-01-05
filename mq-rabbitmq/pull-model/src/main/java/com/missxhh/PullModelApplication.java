package com.missxhh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/***
 * RabbitMq 从队列中拉取消息并消费
 * 应用说明： 优先队列的应用，模拟在消息有优先级的情况下，发出消息后消费者手动拉取优先级较高的消息进行处理
 * @author hjf
 * @date 2021/1/5
 **/
@SpringBootApplication
public class PullModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(PullModelApplication.class);
    }
}
