package com.missxhh.demo.es.service;

import com.missxhh.demo.es.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleSearchService {

    Page<Article> queryESPage(String title, Pageable pageable);

    String updateESData(Article article);

    String deleteESData(String id);
}
