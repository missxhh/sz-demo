package com.missxhh.server.sms;

import com.missxhh.entity.Order;

public interface ISmsService {

    // 推送订单信息
    String pushOrderSms(Order order);

}
