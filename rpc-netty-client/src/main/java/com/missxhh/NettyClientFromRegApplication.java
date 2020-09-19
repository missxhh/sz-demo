package com.missxhh;

import com.missxhh.entity.Order;
import com.missxhh.server.NettyServerDiscover;
import com.missxhh.server.NettyServerDiscoverFromReg;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.store.IStoreService;

import java.math.BigDecimal;

/**
 * 业务客户端
 * @author hjf
 **/
public class NettyClientFromRegApplication {

    public static void main(String[] args) {
        addOrder();
    }

    /***
     * 添加订单
     **/
    private static void addOrder() {

        // 添加订单 ==》 扣减库存 ==》 推送消息 ==》 查询订单信息
        NettyServerDiscoverFromReg client = new NettyServerDiscoverFromReg();
        Order order = new Order();
        order.setId(1L);
        order.setGoodId(1L);
        order.setMoney(new BigDecimal(500));
        order.setUserId("A001");
        order.setNum(100);

        // 添加订单
        IOrderService orderService = client.getRemoteProxyServer(IOrderService.class);
        orderService.addOrder(order);
        // 扣减库存
        IStoreService storeService = client.getRemoteProxyServer(IStoreService.class);
        storeService.deleteGoods(1L);
        // 推送短信消息
        ISmsService smsService = client.getRemoteProxyServer(ISmsService.class);
        String smsInfo = smsService.pushOrderSms(order);

        // 打印短信信息
        System.out.println(smsInfo);

        // 查询订单信息，由于是模拟数据（非数据库提取），多服务的情况，若和上面添加订单请求的服务不一致的情况下可能查询失败，不做处理，知道即可
        Order queryOrder = orderService.getOrder(order.getId());
        if(queryOrder != null) {
            System.out.println("订单详情，订单Id：" + queryOrder.getId() + "，商品Id：" + queryOrder.getGoodId() + "，订单价格：" + queryOrder.getMoney() + "，购买数量：" + queryOrder.getNum() + "，购买人：" + queryOrder.getUserId());
        } else {
            System.out.println("订单查询失败");
        }

        // 保持连接
        while(true) {}
    }
}
