package com.missxhh;


import com.missxhh.server.NettyServerExposeToReg;

/**
 * Netty服务
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
