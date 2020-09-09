package com.missxhh;

import com.missxhh.server.NioServerExpose;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.order.impl.OrderServiceImpl;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.sms.impl.SmsServiceImpl;
import com.missxhh.server.store.IStoreService;
import com.missxhh.server.store.impl.StoreServiceImpl;

/**
 * NIO服务
 * @author hjf
 **/
public class NioServerApplication {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动RPC服务，对外暴露三个服务
                NioServerExpose serverExpose = new NioServerExpose();
                serverExpose.registerService(IOrderService.class.getName(), OrderServiceImpl.class);
                serverExpose.registerService(IStoreService.class.getName(), StoreServiceImpl.class);
                serverExpose.registerService(ISmsService.class.getName(), SmsServiceImpl.class);
                serverExpose.start();
            }
        }).start();
    }
}
