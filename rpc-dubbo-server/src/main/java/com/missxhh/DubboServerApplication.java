package com.missxhh;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Dubbo服务
 * @author hjf
 **/
public class DubboServerApplication {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext("com.missxhh");
        annotationConfigApplicationContext.start();

        System.out.println("============= RPC Dubbo 服务端启动成功，使用端口：" + PORT + " =============");

    }
}
