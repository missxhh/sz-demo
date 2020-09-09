package com.missxhh.data;

import com.missxhh.entity.Order;

import java.util.HashMap;
import java.util.Map;

/**
 * 简易订单数据存储
 **/
public class OrderData {

    private Map<Long, Order> data;

    private static OrderData instance;

    public static OrderData getInstance(){
        if(instance == null) {
            instance = new OrderData();
        }
        return instance;
    }

    private OrderData(){
        this.data = new HashMap<>();
    }

    /**
     * 添加订单
     **/
    public void addOrder(Order order){
        data.put(order.getId(), order);
    }

    /**
     * 查询订单
     **/
    public Order getOrder(Long id){
        return data.get(id);
    }

    /**
     * 删除订单
     **/
    public void deleteOrder(Long id) {
        data.remove(id);
    }
}
