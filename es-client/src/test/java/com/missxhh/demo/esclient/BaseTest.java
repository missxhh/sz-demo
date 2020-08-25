package com.missxhh.demo.esclient;

import com.missxhh.demo.esclient.model.Article;
import com.missxhh.demo.esclient.service.ESClientSearchService;
import com.missxhh.demo.esclient.service.impl.ESClientSearchServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {

    private Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @Autowired
    private ESClientSearchServiceImpl service;

    // 索引名称
    private String INDEX = "article";
    // 类型
    private String TYPE = "doc";
    // 文章
    private Article article;

    private String SEARCH_KEYWORD = "你好";
    private String SEARCH_KEYWORD2 = "ES";

    @Before
    public void init(){
        // 初始化测试数据
        article = new Article();
        article.setId("1");
        article.setTitle("博客1");
        article.setKeywords("ES");
        article.setDescription("我的第一篇博客，开门红");
        article.setContent("文章内容，此处省略一万字");
    }

    @Test
    public void specialTest(){
        try {
            // service.prefixQuery(INDEX, "description", "博");
            // service.MatchPhraseQueryBuilder(INDEX, "description", "博");
            // service.wildcardQuery(INDEX, "description", "好*");
            // service.fuzzyQuery(INDEX, "content", "a");
            // service.regexpQuery(INDEX, "description", "*a");
            // service.mGetQuery(INDEX, TYPE, "1", "2");
            // service.bulkRequest(INDEX, TYPE, article);
            // service.doAnalyzer("ik_smart", "大家好，才是真的好");
             service.collapseQuery(INDEX, TYPE, "keywords.keyword");
            // service.exportDocToFile(INDEX, "D:/out.txt");
        } catch (Exception e) {
            logger.error("索引测试异常", e);
        }
    }

    @Ignore
    public void dslTest(){
        try {
            // service.matchAllQuery(INDEX);
            // service.matchQuery(INDEX, "description", SEARCH_KEYWORD);
            // service.termQuery(INDEX, "keywords", SEARCH_KEYWORD2);
            // service.termsQuery(INDEX, "keywords", SEARCH_KEYWORD2);
            //  service.rangeQuery(INDEX, "id", "1", "30");
            // service.multiMatchQuery(INDEX, "你好", "title", "content");
            // service.commonQuery(INDEX, SEARCH_KEYWORD, "content", 0.0001f);
            // service.existsQuery(INDEX, "id");
            // service.idsQuery(INDEX, TYPE, "1", "2");
            // service.scrollQuery(INDEX, "content", SEARCH_KEYWORD);
            // service.boolQuery();
            // service.constantScoreQuery(INDEX, "content", SEARCH_KEYWORD);
            // service.highlighter(INDEX, "description", "博");
            // service.disMaxQuery(INDEX, "description", "博");
            // service.spanQuery(INDEX, "description", "博");
            // service.typeQuery(INDEX, TYPE);
            service.boostQuery(INDEX, "description", "博");
        } catch (Exception e) {
            logger.error("索引测试异常", e);
        }
    }

    @Ignore
    public void queryStringTest(){
        try {
            service.queryString(INDEX);
        } catch (Exception e) {
            logger.error("索引测试异常", e);
        }
    }

    @Ignore
    public void docTest(){
        try {
            // service.indexDoc(INDEX, TYPE, article.getId(), article);
            // service.updateDoc(INDEX, TYPE, article.getId(), article);
            // service.deleteDocById(INDEX, TYPE, article.getId());
            service.getDocById(INDEX, TYPE, article.getId());
            service.getDocPage(INDEX, TYPE, 0, 10);
        } catch (Exception e) {
            logger.error("索引测试异常", e);
        }
    }

    @Ignore
    public void indexTest(){
        try {
            service.deleteIndex(INDEX);
            service.createIndexWithMapping(INDEX, TYPE);
            service.indexIsExists(INDEX);
        } catch (Exception e) {
            logger.error("索引测试异常", e);
        }
    }
}
