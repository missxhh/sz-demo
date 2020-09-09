package com.missxhh.entity;

import java.io.Serializable;

public class Goods implements Serializable {

    // 商品Id
    private Long id;

    // 商品数量
    private int num;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
