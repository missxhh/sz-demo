package com.missxhh.demo.es.dao;

import com.missxhh.demo.es.model.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleDao extends ElasticsearchRepository <Article, String > {

}
