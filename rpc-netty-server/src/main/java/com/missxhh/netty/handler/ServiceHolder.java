package com.missxhh.netty.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceHolder {

    private static ServiceHolder holder;

    // 服务容器
    private static Map<String, Class> serviceHolder = new HashMap<String, Class>();

    private ServiceHolder(){
        this.serviceHolder = new HashMap<>();
    }

    public static ServiceHolder getInstance(){
        if(holder == null) {
            holder = new ServiceHolder();
        }
        return holder;
    }

    public synchronized Class get(String name) {
        return this.serviceHolder.get(name);
    }

    public synchronized void put(String name, Class clazz) {
        this.serviceHolder.put(name, clazz);
    }
}
