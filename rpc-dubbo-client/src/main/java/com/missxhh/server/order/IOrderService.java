package com.missxhh.server.order;

import com.missxhh.entity.Order;

public interface IOrderService {

    // 添加订单
    void addOrder(Order order);

    // 获取订单
    Order getOrder(Long id);

    // 删除订单
    void deleteOrder(Long id);
}