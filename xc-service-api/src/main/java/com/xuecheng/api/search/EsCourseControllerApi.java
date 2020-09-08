package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/8 19:38
 */
@Api(value = "课程搜索", description = "基于ES构建的课程搜索API",tags = {"课程搜索"})
public interface EsCourseControllerApi {
    @ApiOperation("查询所有课程列表")
    QueryResponseResult<CoursePub> findList(int page, int size, CourseSearchParam courseSearchParam);

    @ApiOperation("根据id搜索课程发布信息")
    Map<String,CoursePub> findById(String id);

    @ApiOperation("根据课程计划查询媒资信息")
    TeachplanMediaPub getMedia(String teachplanId);
}
