package com.missxhh.server;


import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 客户端Dubbo服务初始化（XML方式）
 **/
public class DubboServerDiscoverByXml extends DubboServerDiscover {
    public DubboServerDiscoverByXml(){
        ctx = new ClassPathXmlApplicationContext("classpath:dubbo.xml");
        ctx.refresh();
        System.out.println("============= RPC Dubbo 客户端启动成功 =============");
    }
}
