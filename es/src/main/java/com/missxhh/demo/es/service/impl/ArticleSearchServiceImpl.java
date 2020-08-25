package com.missxhh.demo.es.service.impl;

import com.missxhh.demo.es.dao.ArticleDao;
import com.missxhh.demo.es.mapper.ArticleMapper;
import com.missxhh.demo.es.model.Article;
import com.missxhh.demo.es.service.ArticleSearchService;
import com.missxhh.demo.es.util.ReflectUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleDao articleDao;

    private Logger logger = LoggerFactory.getLogger(ArticleSearchServiceImpl.class);

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /***
     * 数据初始化
     * @Author hjf
     * @Date 2019/12/8
     **/
    @PostConstruct
    public void initData() {
        try {
            logger.info("----------开始初始化ES服务器采购单信息----------");
            // 1、获取数据库中获取所有文章
            List<Article> list = articleMapper.findAll();
            if (list == null || list.size() == 0) {
                return;
            }

            // 2、看ES中是否有数据
            Iterable<Article> iterable = articleDao.findAll();
            if (iterable.iterator().hasNext()) {
                // 已经存在数据，直接Return 不进行处理
                return;
            }
            // 3、若不存在将文章导入ES 中
            for (Article item : list) {
                articleDao.save(item);
            }
            logger.info("数据初始化成功");
        } catch (Exception ex) {
            logger.error("数据初始化失败", ex);
        }
    }

    /***
     * 文章数据分页查询
     * @Author hjf
     * @Date 2019/12/8
     **/
    @Override
    public Page<Article> queryESPage(String title, Pageable pageable) {

        NativeSearchQuery nativeSearchQuery = null;
        if (StringUtils.isEmpty(title)) {
            nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.boolQuery()).build();
        } else {
            nativeSearchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchQuery("title", title))
                    .withHighlightFields(new HighlightBuilder.Field("title")
                            .preTags("<span style=\"color:red\">").postTags("</span>")).build();
        }
        nativeSearchQuery.setPageable(pageable);
        Page<Article> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, Article.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                // 重写返回信息
                ArrayList<Article> lists = new ArrayList<Article>();
                SearchHits hits = searchResponse.getHits();
                // 判断是否有没有命中
                if (hits.getHits().length <= 0) {
                    return null;
                }
                for (SearchHit searchHit : hits) {
                    // 将参数封装成实体 此处简单写，需要优化的可以考虑反射等通用的写法
                    Article item = new Article();
                    item.setId(searchHit.getId());
                    item.setCreateBy(getValue(searchHit, "createBy"));
                    item.setUpdateBy(getValue(searchHit, "updateBy"));
                    item.setTitle(getValue(searchHit, "title"));
                    item.setContent(getValue(searchHit, "content"));
                    item.setDescription(getValue(searchHit, "description"));
                    item.setKeywords(getValue(searchHit, "keywords"));
                    item.setCoverImg(getValue(searchHit, "coverImg"));
                    item.setLinkUrl(getValue(searchHit, "linkUrl"));

                    // 以下设置高亮部分的内容
                    try {
                        if (searchHit.getHighlightFields() != null && searchHit.getHighlightFields().containsKey("title")) {
                            String highLightMessage = searchHit.getHighlightFields().get("title").fragments()[0].toString();
                            String setMethodName = ReflectUtil.parSetName("title");
                            Class<? extends Article> poemClazz = item.getClass();
                            Method setMethod = poemClazz.getMethod(setMethodName, String.class);
                            setMethod.invoke(item, highLightMessage);
                        } else {
                            item.setTitle(getValue(searchHit, "title"));
                        }
                    } catch (Exception ex) {
                        logger.error("设置高亮信息失败", ex);
                    }
                    lists.add(item);
                }
                if (lists.size() > 0) {
                    return new AggregatedPageImpl<T>((List<T>) lists,pageable,searchResponse.getHits().totalHits,
                            searchResponse.getAggregations(),searchResponse.getScrollId());
                }
                return null;
            }
        });
        return page;
    }

    private String getValue(SearchHit searchHit, String key) {
        if (StringUtils.isEmpty(key) || searchHit == null || searchHit.getSourceAsMap() == null) {
            return "";
        }
        return String.valueOf(searchHit.getSourceAsMap().get(key) == null ? "" : searchHit.getSourceAsMap().get(key));
    }

    @Override
    public String updateESData(Article article) {
        if(article == null) {
            return "更新文档未指定";
        }
        try {
            Article articleOld = articleDao.findById(article.getId()).get();

            if(articleOld == null) {
                return "编辑失败，文档不存在";
            }
            articleOld.setLinkUrl(article.getLinkUrl());
            articleOld.setKeywords(article.getKeywords());
            articleOld.setDescription(article.getDescription());
            articleDao.save(articleOld);
        } catch (Exception e) {
            logger.error("更新ES数据失败", e);
            return "更新ES数据失败，异常原因：" + e.getMessage();
        }
        return "";
    }

    @Override
    public String deleteESData(String id) {
        try {
            articleDao.deleteById(id);
        } catch (Exception e) {
            logger.error("删除ES文档失败", e);
            return "删除ES数据失败，异常原因：" + e.getMessage();
        }
        return "";
    }
}
