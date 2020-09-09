package com.missxhh.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {

    // 订单Id
    private Long id;

    // 商品Id
    private Long goodId;

    // 订单价格
    private BigDecimal money;

    // 购买数量
    private int num;

    // 用户Id
    private String userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
