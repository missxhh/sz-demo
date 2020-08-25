package com.missxhh.demo.es.mapper;


import com.missxhh.demo.es.model.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper {

    /***
     * @Description 查找所有文章
     * @Author hjf
     * @Date 2019/12/8 14:21
     * @Param []
     * @return java.util.List<com.missxhh.demo.es.model.Article>
     **/    
    public List<Article> findAll();
}
