package com.missxhh.server;


import com.missxhh.config.ClientAppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 客户端Dubbo服务初始化（注解方式）
 **/
public class DubboServerDiscoverByAnnotation extends DubboServerDiscover {

    public DubboServerDiscoverByAnnotation() {
        ctx = new AnnotationConfigApplicationContext();
        ((AnnotationConfigApplicationContext)ctx).register(ClientAppConfig.class);
        ctx.refresh();
        System.out.println("============= RPC Dubbo 客户端启动成功 =============");
    }
}
