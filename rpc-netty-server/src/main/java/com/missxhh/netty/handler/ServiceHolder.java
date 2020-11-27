package com.missxhh.netty.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceHolder {

    private static ServiceHolder holder;

    // 服务容器
    private static Map<String, Class> serviceHolder = new HashMap<String, Class>();

    private ServiceHolder(){
        serviceHolder = new HashMap<>();
    }

    public static ServiceHolder getInstance(){
        if(holder == null) {
            holder = new ServiceHolder();
        }
        return holder;
    }

    public synchronized Class get(String name) {
        return serviceHolder.get(name);
    }

    public synchronized void put(String name, Class clazz) {
        serviceHolder.put(name, clazz);
    }

    /**
     * 获取所有的服务名
     * @author hjf
     **/
    public String getAllServiceName() {
        String res = "";
        for (String name : serviceHolder.keySet()) {
            res += name + ",";
        }
        if(res.length() > 0) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }
}
