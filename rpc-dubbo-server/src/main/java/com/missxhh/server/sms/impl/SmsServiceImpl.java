package com.missxhh.server.sms.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.missxhh.entity.Order;
import com.missxhh.server.sms.ISmsService;

@Service
public class SmsServiceImpl implements ISmsService {

    @Override
    public String pushOrderSms(Order order) {
        String smsInfo = "用户" + order.getUserId() + "，你好，您的订单" + order.getId() + "已受理完成，我们会及时安排发货！";
        System.out.println(smsInfo);
        return smsInfo;
    }

}
