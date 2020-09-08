package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.fetch.subphase.highlight.Highlighter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/8 19:43
 */
@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String courseIndex;
    @Value("${xuecheng.course.type}")
    private String courseType;
    @Value("${xuecheng.course.source_field}")
    private String courseSourceField;
    @Value("${xuecheng.media.index}")
    private String mediaIndex;
    @Value("${xuecheng.media.type}")
    private String mediaType;
    @Value("${xuecheng.media.source_field}")
    private String mediaSourceField;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public QueryResponseResult<CoursePub> findList(int page, int size, CourseSearchParam courseSearchParam) {
        //参数校验
        if(courseSearchParam == null){
            courseSearchParam = new CourseSearchParam();
        }
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(courseIndex);
        searchRequest.types(courseType);
        //创建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //过虑源字段
        String[] sourceFieldArrays = courseSourceField.split(",");
        searchSourceBuilder.fetchSource(sourceFieldArrays,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //根据关键字查询
        String keyword = courseSearchParam.getKeyword();
        if(StringUtils.isNotEmpty(keyword)){
            //创建多属性查询
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name",10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //一级分类
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        //设置boolQueryBuilder到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //分页查询
        if(page <= 0){
            page = 1;
        }
        if(size <= 0){
            size = 16;
        }
        int from = (page - 1) * size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        //准备返回值
        QueryResult<CoursePub> coursePubQueryResult = new QueryResult<>();
        //数据列表
        List<CoursePub> CoursePubList = new ArrayList<>();
        try {
            //发送搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //获取响应结果
            SearchHits hits = searchResponse.getHits();
            //设置数据总数
            long total = hits.getTotalHits();
            coursePubQueryResult.setTotal(total);
            SearchHit[] searchHits = hits.getHits();
            //遍历搜索数据
            for(SearchHit searchHit:searchHits){
                CoursePub coursePub = new CoursePub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                //组装对象
                //取出id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                //取出name
                String name = (String) sourceAsMap.get("name");
                //设置高亮name
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                //如果没有高亮直接设置name
                if(highlightFields.get("name") != null){
                    HighlightField nameHighlightField = highlightFields.get("name");
                    Text[] fragments = nameHighlightField.getFragments();
                    StringBuilder stringBuilder = new StringBuilder();
                    for(Text fragment:fragments){
                        stringBuilder.append(fragment);
                    }
                    name = stringBuilder.toString();
                }
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price")!=null ){
                        price = (Double) sourceAsMap.get("price");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                Double priceOld = null;
                try {
                    if(sourceAsMap.get("priceOld")!=null ){
                        priceOld = (Double) sourceAsMap.get("priceOld");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(priceOld);
                //讲对象添加到集合中
                CoursePubList.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        coursePubQueryResult.setList(CoursePubList);
        return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,coursePubQueryResult);
    }

    /**
     * 根据id查询课程
     * @param id id
     * @return json数据
     */
    public Map<String, CoursePub> findById(String id) {
        //参数校验
        if(id == null || StringUtils.isEmpty(id)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //定义搜索请求对象
        SearchRequest searchRequest = new SearchRequest(courseIndex);
        //设置类型
        searchRequest.types(courseType);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //根据单一属性进行查询
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));
        //过虑源字段，不用设置源字段，取出所有字段
        //设置定义SearchSourceBuilder
        searchRequest.source(searchSourceBuilder);
        //进行查询
        Map<String, CoursePub> stringCoursePubHashMap = new HashMap<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            //进行遍历
            for(SearchHit searchHit:searchHits){
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                CoursePub coursePub = new CoursePub();
                coursePub.setId((String) sourceAsMap.get("id"));
                coursePub.setName((String) sourceAsMap.get("name"));
                coursePub.setPic((String) sourceAsMap.get("pic"));
                coursePub.setGrade((String) sourceAsMap.get("grade"));
                coursePub.setTeachplan((String) sourceAsMap.get("teachplan"));
                coursePub.setDescription((String) sourceAsMap.get("description"));
                stringCoursePubHashMap.put(id,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringCoursePubHashMap;
    }

    /**
     * 根据课程计划id查询
     * @param teachplanIds 课程计划id
     * @return 课程计划和媒资关联对象
     */
    public QueryResponseResult<TeachplanMediaPub> getMedia(String[] teachplanIds) {
        //参数校验
        if(teachplanIds == null ){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //定义搜索请求对象
        SearchRequest searchRequest = new SearchRequest(mediaIndex);
        //定义类型
        searchRequest.types(mediaType);
        //定义根据单一属性查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义term查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //定义过滤对象
        String[] mediaSourceFields = mediaSourceField.split(",");
        searchSourceBuilder.fetchSource(mediaSourceFields,new String[]{});
        //定义查询方法
        searchRequest.source(searchSourceBuilder);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        //定义数据总数
        long total = 0;
        try {
            //进行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            total = hits.totalHits;
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit searchHit:searchHits){
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                //将属性组装
                teachplanMediaPub.setTeachplanId((String) sourceAsMap.get("teachplan_id"));
                teachplanMediaPub.setMediaId((String) sourceAsMap.get("media_id"));
                teachplanMediaPub.setMediaFileOriginalName((String) sourceAsMap.get("media_fileoriginalname"));
                teachplanMediaPub.setMediaUrl((String) sourceAsMap.get("media_url"));
                teachplanMediaPub.setCourseId((String) sourceAsMap.get("courseid"));
                teachplanMediaPubs.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResult<TeachplanMediaPub> teachplanMediaPubQueryResult = new QueryResult<>();
        teachplanMediaPubQueryResult.setList(teachplanMediaPubs);
        teachplanMediaPubQueryResult.setTotal(total);
        return new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS,teachplanMediaPubQueryResult);
    }
}
