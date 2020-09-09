package com.missxhh;


import com.missxhh.server.NettyServerExpose;

/**
 * Netty服务
 * @author hjf
 **/
public class NettyServerApplication {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyServerExpose serverExpose = new NettyServerExpose();
                serverExpose.start();
            }
        }).start();
    }
}
