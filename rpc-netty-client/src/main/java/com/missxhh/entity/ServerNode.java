package com.missxhh.entity;

import java.io.Serializable;

/***
 * 服务节点
 * @author hjf
 **/
public class ServerNode implements Serializable {

    private static final Long serialVersionUID = 1L;

    private String serverHost;
    private int serverPort;

    public ServerNode(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
