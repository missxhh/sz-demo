package com.missxhh.server;

import org.springframework.context.support.AbstractApplicationContext;

/***
 * 客户端Dubbo服务抽象类
 * @author hjf
 **/
public abstract class DubboServerDiscover {

    protected AbstractApplicationContext ctx;

    public <T> T getBean(Class<T> clazz) {
        if(ctx != null) {
            return ctx.getBean(clazz);
        }
        return null;
    }
}
