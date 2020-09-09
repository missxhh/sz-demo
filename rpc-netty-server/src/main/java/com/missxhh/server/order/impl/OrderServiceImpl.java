package com.missxhh.server.order.impl;

import com.missxhh.data.OrderData;
import com.missxhh.entity.Order;
import com.missxhh.server.order.IOrderService;

public class OrderServiceImpl implements IOrderService {

    @Override
    public void addOrder(Order order) {
        if(order == null) {
            return;
        }
        OrderData.getInstance().addOrder(order);
        System.out.println("添加订单成功，订单Id：" + order.getId() + "，商品Id：" + order.getGoodId() + "，订单价格：" + order.getMoney() + "，购买数量：" + order.getNum() + "，购买人：" + order.getUserId());
    }

    @Override
    public Order getOrder(Long id) {
        System.out.println("获取订单，订单Id：" + id);
        Order order = OrderData.getInstance().getOrder(id);
        return order;
    }

    @Override
    public void deleteOrder(Long id) {
        OrderData.getInstance().deleteOrder(id);
        System.out.println("删除订单成功，订单Id：" + id);
    }
}
