package com.missxhh.server.store.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.missxhh.entity.Goods;
import com.missxhh.server.store.IStoreService;

@Service
public class StoreServiceImpl implements IStoreService {

    @Override
    public void addGoods(Goods goods) {
        System.out.println("添加商品，商品Id：" + goods.getId() + "，商品数量：" + goods.getNum());
    }

    @Override
    public void deleteGoods(Long id) {
        System.out.println("删除商品，商品Id：" + id);
    }

    @Override
    public Goods queryGoods(Long id) {
        System.out.println("查询商品，商品Id：" + id);
        Goods goods = new Goods();
        goods.setId(id);
        goods.setNum(100);
        return goods;
    }
}
