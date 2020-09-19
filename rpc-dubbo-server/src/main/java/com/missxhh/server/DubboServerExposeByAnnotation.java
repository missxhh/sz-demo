package com.missxhh.server;


import com.missxhh.config.ServerAppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Dubbo服务暴露(注解方式)
 * @author hjf
 **/
public class DubboServerExposeByAnnotation extends DubboServerExpose {

    /**
     * 服务启动
     * @author hjf
     **/
    public DubboServerExposeByAnnotation() {
        ctx = new AnnotationConfigApplicationContext();
        ((AnnotationConfigApplicationContext)ctx).register(ServerAppConfig.class);
        ctx.refresh();
        System.out.println("============= RPC Dubbo 服务端启动成功 =============");
    }
}