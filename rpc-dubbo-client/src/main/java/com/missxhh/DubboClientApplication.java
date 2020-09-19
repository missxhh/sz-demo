package com.missxhh;

import com.missxhh.entity.Order;
import com.missxhh.server.DubboServerDiscover;
import com.missxhh.server.DubboServerDiscoverByAnnotation;
import com.missxhh.server.DubboServerDiscoverByXml;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.store.IStoreService;

import java.math.BigDecimal;

/**
 * 业务客户端
 * @author hjf
 **/
public class DubboClientApplication {

    public static void main(String[] args) {
        addOrder();
    }

    /***
     * 添加订单
     **/
    private static void addOrder() {

        DubboServerDiscover clientApp = null;
        // 通过注解启动
        // clientApp = new DubboServerDiscoverByAnnotation();
        // 通过xml启动
        clientApp = new DubboServerDiscoverByXml();

        // 添加订单 ==》 扣减库存 ==》 推送消息 ==》 查询订单信息

        Order order = new Order();
        order.setId(1L);
        order.setGoodId(1L);
        order.setMoney(new BigDecimal(500));
        order.setUserId("A001");
        order.setNum(100);

        // 添加订单
        IOrderService orderService = clientApp.getBean(IOrderService.class);
        orderService.addOrder(order);
        // 扣减库存
        IStoreService storeService = clientApp.getBean(IStoreService.class);
        storeService.deleteGoods(1L);
        // 推送短信消息
        ISmsService smsService = clientApp.getBean(ISmsService.class);
        String smsInfo = smsService.pushOrderSms(order);

        // 打印短信信息
        System.out.println(smsInfo);

        // 查询订单信息
        Order queryOrder = orderService.getOrder(order.getId());
        if(queryOrder != null) {
            System.out.println("订单详情，订单Id：" + queryOrder.getId() + "，商品Id：" + queryOrder.getGoodId() + "，订单价格：" + queryOrder.getMoney() + "，购买数量：" + queryOrder.getNum() + "，购买人：" + queryOrder.getUserId());
        } else {
            System.out.println("订单查询失败");
        }
    }
}
