package com.missxhh.server;


import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Dubbo服务暴露(XML方式)
 * @author hjf
 **/
public class DubboServerExposeByXml extends DubboServerExpose {

    /**
     * 服务启动
     * @author hjf
     **/
    public DubboServerExposeByXml() {
        ctx = new ClassPathXmlApplicationContext("classpath:dubbo.xml");
        ctx.refresh();
        System.out.println("============= RPC Dubbo 服务端启动成功 =============");
    }
}