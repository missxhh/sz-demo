package com.missxhh.demo.esclient.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.missxhh.demo.esclient.model.Article;
import com.missxhh.demo.esclient.service.ESClientSearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;

/**
 * ES搜索服务
 * @Author hjf
 * @Date 2019/12/8
 **/
@Service
public class ESClientSearchServiceImpl implements ESClientSearchService {

    private Logger logger = LoggerFactory.getLogger(ESClientSearchServiceImpl.class);

    @Autowired
    private TransportClient transportClient;

    private ObjectMapper jsonMapper = new ObjectMapper();

    // -------------------------索引操作-----------------------------------

    /***
     * 自定义映射
     * @Author hjf
     * @Date 2019/12/8 20:38
     **/
    public void createIndexWithMapping(String index, String type) throws Exception{
        CreateIndexRequestBuilder createIndexRequestBuilder = transportClient.admin().indices().prepareCreate(index);
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id").field("type","text").endObject()
                .startObject("title").field("type","text").endObject()
                .startObject("description").field("type","text").endObject()
                .startObject("keywords").field("type","keyword").endObject()
                .startObject("content").field("type","text").endObject()
                .endObject()
                .endObject();
        createIndexRequestBuilder.addMapping(type, mapping);
        CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
        logger.info("添加映射成功");
    }

    /***
     * 索引是否存在
     * @Author hjf
     * @Date 2019/12/8 20:50
     **/
    public void indexIsExists(String index) throws Exception{
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
        IndicesExistsResponse response = transportClient.admin().indices().exists(inExistsRequest).actionGet();
        String isExist = response.isExists()? "存在" : "不存在";
        System.out.println("索引【" + index + "】" + isExist);
    }

    /**
     * 删除索引
     * @Author hjf
     * @Date 2019/12/8 20:50
     **/
    public void deleteIndex(String index) throws Exception{
        DeleteIndexResponse response = transportClient.admin().indices().prepareDelete(index).execute().actionGet();
        System.out.println("删除执行状态:" + response.isAcknowledged());
    }

    // -------------------------文档基础操作-----------------------------------

    /***
     * 索引文档
     * @Author hjf
     * @Date 2019/12/8 20:38
     **/
    public void indexDoc(String index, String type, String id, Object doc) throws Exception{
        String source = jsonMapper.writeValueAsString(doc);
        /****
         * 此处使用json，使用 XContentBuilder 可以是：
         * XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject()
         *                 .field("xxx", "xxx")
         *                 .endObject();
         * ***/
        IndexResponse response = transportClient.prepareIndex(index, type, id).setSource(source, XContentType.JSON).get();
        System.out.println("索引文档成功：" + response.status());
    }

    /***
     * 删除指定文档
     * @Author hjf
     * @Date 2019/12/8 20:51
     **/
    public void deleteDocById(String index, String type, String id) throws Exception{
        DeleteResponse response = transportClient.prepareDelete(index, type, id).execute().actionGet();
        System.out.println("删除执行状态:" + response.status());
    }

    /***
     * 更新文档
     * @Author hjf
     * @Date 2019/12/8 20:51
     **/
    public void updateDoc(String index, String type, String id, Object doc) throws Exception{
        String updateDoc = jsonMapper.writeValueAsString(doc);

        //创建修改请求
        UpdateRequest updateRequest = new UpdateRequest().index(index).type(type).id(id);
        updateRequest.doc(updateDoc, XContentType.JSON);
        // 直接更新
        UpdateResponse response = transportClient.update(updateRequest).get();
        System.out.println("文档直接更新状态：" + response.status());
        // prepare 方式更新，字段写死，仅供参考
        XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject()
                .field("title", "博客更新1")
                .endObject();
        response = transportClient.prepareUpdate(index, type, id).setDoc(contentBuilder).get();
        System.out.println("文档prepare更新状态：" + response.status());
    }

    /***
     * 通过 Id 获取文档
     * @Author hjf
     * @Date 2019/12/8 21:02
     **/
    public void getDocById(String index, String type, String id)  throws Exception {
        GetResponse response = transportClient.prepareGet(index, type, id).execute().actionGet();
        String source = response.getSourceAsString();
        if (!StringUtils.isEmpty(source)) {
            logger.info("查询成功，结果为：" + source);
        } else {
            System.out.println("没找到文档");
        }
    }

    /***
     * 分页查询：from .. to ...
     * @Author hjf
     * @Date 2019/12/8 10:15
     **/
    public void getDocPage(String index, String type, int from, int to) throws Exception {
        SearchResponse response = transportClient.prepareSearch(index)
                .setTypes(type)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(from)
                .setSize(to)
                .execute().actionGet();
        for (SearchHit searchHit : response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    // -------------------------查询字符串-----------------------------------

    /***
     * 查询字符串
     * @Author hjf
     * @Date 2019/12/8 21:35
     **/
    public void queryString(String index) throws Exception{
        // 指定：+ 包含，- 文档，* 所有
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("*:*");
        // QueryBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery("+id:1");
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }


    // -------------------------DSL-----------------------------------

    /***
     * 查询全部: match_all
     * @Author hjf
     * @Date 2019/12/8 21:13
     **/
    public  void matchAllQuery(String index) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit searchHit : response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 关键字查询：match
     * @Author hjf
     * @Date 2019/12/8 21:17
     **/
    public  void matchQuery(String index, String field, String value) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, value);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit searchHit : response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 精确查询：term
     * @Author hjf
     * @Date 2019/12/8 21:19
     **/
    public void termQuery(String index, String field, String value) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field, value);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 精确查询多个值：terms
     * @Author hjf
     * @Date 2019/12/8 21:20
     **/
    public void termsQuery(String index, String field, String ...values) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.termsQuery(field, values);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 范围查询：range
     * @Author hjf
     * @Date 2019/12/8 21:21
     **/
    public void rangeQuery(String index, String field, String from, String to) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery(field).from(from).to(to);
        // 或者 QueryBuilders.rangeQuery("id").gte("1").lt("30");
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 多字段查询：multi_match
     * @Author hjf
     * @Date 2019/12/8 21:24
     **/
    public void multiMatchQuery(String index, String value, String ...fields) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(value, fields);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 常用词查询：common
     * @Author hjf
     * @Date 2019/12/8 21:28
     **/
    public void commonQuery(String index, String value, String field, float cutoffFrequency) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.commonTermsQuery(field, value).cutoffFrequency(cutoffFrequency);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 存在查询：exists
     * @Author hjf
     * @Date 2019/12/8 21:35
     **/
    public void existsQuery(String index, String field) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.existsQuery(field);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 多Id 查询：ids
     * @Author hjf
     * @Date 2019/12/8 10:12
     **/
    public void idsQuery(String index, String type, String ...ids) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.idsQuery(type).addIds(ids);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 组合查询：bool
     * 非工具类，语法展示
     * @Author hjf
     * @Date 2019/12/8 10:13
     **/
    public void boolQuery(String index, String value) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("title", value))
                .must(QueryBuilders.termQuery("content", value))
                .mustNot(QueryBuilders.termQuery("content", value))
                .should(QueryBuilders.termQuery("keywords",value))
                .filter(QueryBuilders.termQuery("title", value));
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).execute().actionGet();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 滚动分页：scroll
     * @Author hjf
     * @Date 2019/12/8 10:15
     **/
    public void scrollQuery(String index, String field, String value) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, value);
        SearchResponse scrollResp = transportClient.prepareSearch(index)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(1).get();
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                System.out.println(hit);
            }
            scrollResp = transportClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while(scrollResp.getHits().getHits().length != 0);
    }

    /***
     * 无相关性查询：constant_score
     * @Author hjf
     * @Date 2019/12/8 10:18
     **/
    public void constantScoreQuery(String index, String field, String value) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.matchQuery(field, value));
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).execute().actionGet();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 高亮查询：high_lighter
     * @Author hjf
     * @Date 2019/12/8 10:20
     **/
    public void highlighter(String index, String field, String value) throws Exception{
        QueryBuilder matchQuery = QueryBuilders.matchQuery(field, value);
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<h2>");
        hiBuilder.postTags("</h2>");
        hiBuilder.field(field);
        // 搜索数据
        SearchResponse response = transportClient.prepareSearch(index)
                .setQuery(matchQuery)
                .highlighter(hiBuilder)
                .execute().actionGet();
        for (SearchHit searchHit : response.getHits()) {
            System.out.println(searchHit.getHighlightFields());
        }
    }

    /***
     * 最佳查询：dis_max
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:27
     **/
    public void disMaxQuery(String index, String field, String value) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.matchQuery(field, value))
                .add(QueryBuilders.termQuery(field, value))
                .boost(1.2f)
                .tieBreaker(0.7f);
        SearchResponse response=transportClient.prepareSearch(index).setQuery(queryBuilder).execute().actionGet();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 跨度查询：span
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:31
     **/
    public void spanQuery(String index, String field, String value) throws Exception{
        // spanFirstQuery
        QueryBuilder queryBuilder =QueryBuilders.spanFirstQuery(QueryBuilders.spanTermQuery(field, value),30000);
        // spanNotQuery
        // QueryBuilder queryBuilder =QueryBuilders.spanNotQuery(QueryBuilders.spanTermQuery(field, value), null);
        // spanOrQuery
        // QueryBuilder queryBuilder =QueryBuilders.spanOrQuery(QueryBuilders.spanTermQuery(field, value));
        // spanNearQuery
        // QueryBuilder queryBuilder =QueryBuilders.spanNearQuery(QueryBuilders.spanTermQuery(field, value),1000)
        //         .addClause(QueryBuilders.spanTermQuery(field, value))
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit searchHit : response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 类型查询：type
     * @Author hjf
     * @Date 2019/12/8 10:19
     **/
    public  void typeQuery(String index, String type) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.typeQuery(type);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 权重查询：boost
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:33
     **/
    public void boostQuery(String index, String field, String value) throws Exception{
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery(field, value)).boost(2)
                .should(QueryBuilders.termQuery(field,value).boost(1));
        // 负增强
        // QueryBuilder qb = QueryBuilders.boostingQuery(
        //         QueryBuilders.termQuery("id",article.getId()),
        //         QueryBuilders.termQuery("title",SEARCH_KEYWORD))
        //         .negativeBoost(0.2f);
        SearchResponse response=transportClient.prepareSearch(index).setQuery(boolQueryBuilder).execute().get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 删除闪现：query_delete
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:38
     **/
    public void deleteByQuery(String index, QueryBuilder queryBuilder)  throws Exception{
        DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
                .source(index)
                .filter(queryBuilder)
                .get();
    }

    /***
     * 混合查询
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:37
     **/
    public void complexSearch2(String index, String type, String path, String field, String value, int page, int pageSize) throws Exception{
        BoolQueryBuilder builders=new BoolQueryBuilder();
        //加上条件
        builders.must(QueryBuilders.termQuery(field, value));
        builders.must(QueryBuilders.nestedQuery(path, QueryBuilders.boolQuery().must(QueryBuilders.termQuery(field, value)), ScoreMode.None));
        SearchResponse response=transportClient.prepareSearch(index).setTypes(type)
                .setQuery(builders).setFrom((page-1)*pageSize)
                .setSize(pageSize)
                .get();
        for (SearchHit searchHit : response.getHits()) {
            System.out.println(searchHit.getHighlightFields());
        }
    }

    // -------------------------特殊匹配-----------------------------------

    /***
     * 前缀匹配：prefix
     * @Author hjf
     * @Date 2019/12/8 21:37
     **/
    public void prefixQuery(String index, String field, String value) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.prefixQuery(field, value);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 短语匹配：match_phrase
     * @Author hjf
     * @Date 2019/12/8 21:38
     **/
    public void MatchPhraseQueryBuilder(String index, String field, String value) throws Exception {
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
        matchPhraseQueryBuilder.slop(2);

        SearchResponse searchResponse = transportClient.prepareSearch()
                .setIndices(index)
                .setQuery(matchPhraseQueryBuilder)
                .execute()
                .actionGet();
        for (SearchHit  searchHit: searchResponse.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 通配符：wildcard
     * @Author hjf
     * @Date 2019/12/8 21:39
     **/
    public void wildcardQuery(String index, String field, String value) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery(field, value);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /**
     * 模糊匹配：fuzzy
     * @Author hjf
     * @Date 2019/12/8 21:40
     **/
    public  void fuzzyQuery(String index, String field, String value) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery(field, value);
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    /***
     * 正则匹配：regexp
     * @Author hjf
     * @Date 2019/12/8 10:11
     **/
    public void regexpQuery(String index, String field, String value) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.regexpQuery(field, value);
        SearchResponse response=transportClient.prepareSearch(index).setQuery(queryBuilder).execute().actionGet();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }
    }

    // -------------------------批量操作-----------------------------------

    /***
     * _mget api
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 21:28
     **/
    public void mGetQuery(String index, String type, String ...ids) throws Exception {
        if(ids.length <= 0) {
            return;
        }
        MultiGetRequest request = new MultiGetRequest();
        for (String item : ids) {
            request.add(new MultiGetRequest.Item(index, type, item));
        }
        MultiGetResponse multiGetItemResponses = transportClient.multiGet(request).actionGet();
        for (MultiGetItemResponse responseItem : multiGetItemResponses) {
            GetResponse response = responseItem.getResponse();
            if (response.isExists()) {
                String source = response.getSourceAsString();
                System.out.println("匹配的文档：" + source);
            }
        }
    }

    /**
     * _bulk api
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:21
     **/
    public void bulkRequest(String index, String type, Article article) throws Exception {
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
        bulkRequest.add(transportClient.prepareIndex(index, type, article.getId())
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", article.getId())
                        .field("title", article.getTitle())
                        .field("description", article.getDescription())
                        .field("content",article.getContent())
                        .field("keywords", article.getKeywords())
                        .endObject()
                )
        );
        bulkRequest.add(transportClient.prepareIndex(index, type, article.getId() + "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", article.getId() + "1")
                        .field("title", article.getTitle())
                        .field("description", article.getDescription())
                        .field("content",article.getContent())
                        .field("keywords", article.getKeywords())
                        .endObject()
                )
        );
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            System.out.println(bulkResponse.getTook());
        }
    }

    /***
     * _bulk api
     * 提交事件
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:21
     **/
    public void autoBulkProcessor(String index, String type, Article article) throws Exception {
        BulkProcessor bulkProcessor = BulkProcessor.builder(transportClient, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                // 批操作前 to do..
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                // 批操作成功后 to do ..
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                // 批操作失败后 to do ..
            }
        })
                .setBulkActions(10000)      //  10000文档时自动提交
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)) // 提交限制大小
                .setFlushInterval(TimeValue.timeValueSeconds(5))    //  提交间隔
                .setConcurrentRequests(1)   // 请求并发数，0：1个请求并行，1：2个请求并行
                // 失败不常策略
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        for(int i=0; i < 1000000; i++){
            article.setId(i+"");
            String doc = jsonMapper.writeValueAsString(article);  // 重复提交
            bulkProcessor.add(new IndexRequest(index, type,i + "").source(doc));
        }
        System.out.println("批操作提交完成");
    }

    /***
     * _bulk api
     * 语法展示
     * @Author hjf
     * @Date 2019/12/8 10:22
     **/
    public void finishAddBulkCommit(String index, String type, Article article) throws Exception {
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
        for(int i = 500; i < 1000; i++){
            article.setId(i+"");
            String doc = jsonMapper.writeValueAsString(article);  // 重复提交
            IndexRequestBuilder indexRequest = transportClient.prepareIndex(index, type).setSource(doc).setId(String.valueOf(i));
            bulkRequest.add(indexRequest);
        }

        // 无自动提交，手动进行
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            System.out.println(bulkResponse.buildFailureMessage());
        }
        System.out.println("批操作提交完成");
    }

    /**
     * _bulk api
     * 批量删除
     * @Author hjf
     * @Date 2019/12/8 10:24
     **/
    public void batchDelete(String index, String type, String ... ids)  throws Exception{
        BulkRequestBuilder builder=transportClient.prepareBulk();
        for(String publishId : ids){
            builder.add(transportClient.prepareDelete(index, type, publishId).request());

        }
        BulkResponse bulkResponse = builder.get();
        System.out.println(bulkResponse.status());
    }


    // -------------------------导入 & 导出-----------------------------------

    /***
     * 导入（按照批格式进行）
     * _bulk
     * @Author hjf
     * @Date 2019/12/8 10:25
     **/
    public void importFromBulkFile(String index, String type, String filePath) throws Exception{
        FileReader fr = null;
        BufferedReader bfr = null;
        String line=null;
        try {
            File file = new File(filePath);
            fr = new FileReader(file);
            bfr = new BufferedReader(fr);
            BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
            int count=0;
            while((line = bfr.readLine()) != null){
                bulkRequest.add(transportClient.prepareIndex(index, type).setSource(line));
                if (count%10==0) {
                    bulkRequest.execute().actionGet();
                }
                count++;
            }
            bulkRequest.execute().actionGet();
            System.out.println("导入成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bfr.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 导出文档到文件
     * @Author hjf
     * @Date 2019/12/8 10:25
     **/
    public void exportDocToFile(String index, String filePath) throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse response = transportClient.prepareSearch(index).setQuery(queryBuilder).get();
        SearchHits resultHits = response.getHits();
        FileWriter fw = null;
        BufferedWriter bfw = null;
        try {
            File file = new File(filePath);
            fw = new FileWriter(file);
            bfw = new BufferedWriter(fw);
            if (resultHits.getHits().length == 0) {
                System.out.println("查到0条数据!");
            } else {
                for (int i = 0; i < resultHits.getHits().length; i++) {
                    String jsonStr = resultHits.getHits()[i].getSourceAsString();
                    System.out.println(jsonStr);
                    bfw.write(jsonStr);
                    bfw.write("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bfw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------------分词查看-----------------------------------

    /**
     * 分词查看
     * @Author hjf
     * @Date 2019/12/8 10:27
     **/
    public void doAnalyzer(String tokenType, String text) throws Exception {
        AnalyzeRequest analyzeRequest = new AnalyzeRequest();
        analyzeRequest.text(text);
        analyzeRequest.analyzer(tokenType);
        ActionFuture<AnalyzeResponse> analyzeResponseActionFuture =  transportClient.admin().indices().analyze(analyzeRequest);
        List<AnalyzeResponse.AnalyzeToken> analyzeTokens =  analyzeResponseActionFuture.actionGet().getTokens();
        for (AnalyzeResponse.AnalyzeToken analyzeToken : analyzeTokens){
            System.out.println(analyzeToken.getTerm());
        }
    }

    // -------------------------折叠-----------------------------------

    public void collapseQuery(String index, String type, String field)  throws Exception{
        CollapseBuilder collapseBuilder = new CollapseBuilder(field);     // 聚合的字段
        InnerHitBuilder innerHitBuilder = new InnerHitBuilder("collapse_inner_hit");
        innerHitBuilder.setSize(1);         // 每个聚合返回的条数
        collapseBuilder.setInnerHits(innerHitBuilder);
        SearchResponse response = transportClient.prepareSearch(index).setCollapse(collapseBuilder).execute().actionGet();
        for (SearchHit  searchHit: response.getHits()) {
            System.out.println(searchHit);
        }
    }
}
