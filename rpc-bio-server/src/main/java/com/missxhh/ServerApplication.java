package com.missxhh;

import com.missxhh.server.ServerExpose;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.order.impl.OrderServiceImpl;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.sms.impl.SmsServiceImpl;
import com.missxhh.server.store.IStoreService;
import com.missxhh.server.store.impl.StoreServiceImpl;

public class ServerApplication {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动RPC服务，对外暴露三个服务
                ServerExpose serverExpose = new ServerExpose();
                serverExpose.registerService(IOrderService.class.getName(), OrderServiceImpl.class);
                serverExpose.registerService(IStoreService.class.getName(), StoreServiceImpl.class);
                serverExpose.registerService(ISmsService.class.getName(), SmsServiceImpl.class);
                serverExpose.start();
            }
        }).start();
    }

}
