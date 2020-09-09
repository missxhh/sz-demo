package com.missxhh.server.store;

import com.missxhh.entity.Goods;

public interface IStoreService {

    // 添加商品
    void addGoods(Goods goods);

    // 删除商品
    void deleteGoods(Long id);

    // 查询商品
    Goods queryGoods(Long id);

}
