package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/8 19:36
 */
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<CoursePub> findList(@PathVariable("page") int page, @PathVariable("size") int size, CourseSearchParam courseSearchParam) {
        return esCourseService.findList(page,size,courseSearchParam);
    }

    /**
     * 根据id查询课程信息
     * @param id id
     * @return json数据
     */
    @Override
    @GetMapping("/getdetail/{id}")
    public Map<String, CoursePub> findById(@PathVariable("id") String id) {
        return esCourseService.findById(id);
    }

    /**
     * 提供查询课程媒资接口，此接口供学习服务调用。
     * @param teachplanId 课程计划id
     * @return 课程计划和媒资关联对象
     */
    @Override
    @GetMapping("/getMedia/{teachplanId}")
    public TeachplanMediaPub getMedia(@PathVariable String teachplanId) {
        //为了service的拓展性,所以我们service接收的是数组作为参数,以便后续开发查询多个ID的接口
        String[] teachplanIds = new String[]{teachplanId};
        //通过service查询ES获取课程媒资信息
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = esCourseService.getMedia(teachplanIds);
        QueryResult<TeachplanMediaPub> queryResult = queryResponseResult.getQueryResult();
        if(queryResult!=null&& queryResult.getList()!=null
                && queryResult.getList().size()>0){
            //返回课程计划对应课程媒资
            return queryResult.getList().get(0);
        } return new TeachplanMediaPub();
    }


}
