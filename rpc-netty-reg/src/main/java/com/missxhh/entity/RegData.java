package com.missxhh.entity;

import java.io.Serializable;

public class RegData implements Serializable {

    private static final Long serialVersionUID = 1L;

    // 类型 1、注册服务 2、查询服务
    private int type;
    // 服务名
    private String serviceName;
    // 服务主机
    private String host;
    // 服务端口
    private int port;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
