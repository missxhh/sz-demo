package com.missxhh;


import com.missxhh.server.NettyServerExposeToReg;

/**
 * Netty服务（注册中心版）
 * @author hjf
 **/
public class NettyServerToRegApplication {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyServerExposeToReg serverExpose = new NettyServerExposeToReg();
                serverExpose.start();
            }
        }).start();
    }
}
