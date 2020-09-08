package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/6 22:05
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Autowired
    RestHighLevelClient client;

    /**
     * 查询所有文档
     */
    @Test
    public void testSearchAll() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //配置source源字段过虑，1显示的，2排除的
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //将搜索源配置到搜索请求中，执行搜索，获取搜索响应结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * 分页查询
     */
    @Test
    public void testSearchPage() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //ES这里是按起始坐标来实现分页查询,所以我们要指定一个页码
        int form = 0;
        int size = 1;
        searchSourceBuilder.from(form);
        searchSourceBuilder.size(size);
        //配置source源字段过虑，1显示的，2排除的
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //将搜索源配置到搜索请求中，执行搜索，获取搜索响应结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * Term Query 精确查询
     */
    @Test
    public void testSearchTermQuery() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"}, new String[]{});
        //将搜索源配置到搜索请求中，执行搜索，获取搜索响应结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
    /**
     * 根据id精确匹配
     */
    @Test
    public void testSearchById() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] ids = {"1", "2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));

        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"}, new String[]{});
        //将搜索源配置到搜索请求中，执行搜索，获取搜索响应结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * match query (匹配单个字段)
     */
    @Test
    public void testMatchQuery() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));

        searchSourceBuilder.fetchSource(new String[]{"name"}, new String[]{});
        //将搜索源配置到搜索请求中，执行搜索，获取搜索响应结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
    /**
     * multiMatch(匹配多个个字段)
     */
    @Test
    public void testMultiMatchQuery() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description").minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);
        searchSourceBuilder.query(multiMatchQueryBuilder);


        searchSourceBuilder.fetchSource(new String[]{"name"}, new String[]{});
        //将搜索源配置到搜索请求中，执行搜索，获取搜索响应结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
    /**
     * 排序查询
     */
    @Test
    public void testSortQuery() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //boolQuery搜索方式
        //定义一个boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义过虑器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);

        //添加排序
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * 过滤器查询
     */
    @Test
    public void testFilterQuery() throws IOException {
        //创建搜索请求对象,并设置类型
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //创建multiMatch查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description").minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //过虑条件
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        //获取所有搜索结果、总匹配数量
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        //遍历结果
        for(SearchHit searchHit:searchHits){
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
}
