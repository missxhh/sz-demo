package com.missxhh.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceHolder {

    private static ServiceHolder holder;

    // 服务容器
    private Map<String, Set<ServerNode>> serviceHolder;

    private ServiceHolder(){
        this.serviceHolder = new HashMap<>();
    }

    public static ServiceHolder getInstance(){
        if(holder == null) {
            holder = new ServiceHolder();
        }
        return holder;
    }

    public synchronized Set<ServerNode> get(String name) {
        return this.serviceHolder.get(name);
    }

    public synchronized void put(String name, Set<ServerNode> set) {
        this.serviceHolder.put(name, set);
    }
}
