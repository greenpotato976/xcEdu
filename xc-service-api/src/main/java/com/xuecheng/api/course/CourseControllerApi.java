package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/22 15:27
 */
@Api(value = "课程管理",description = "课程管理",tags = {"课程管理"})
public interface CourseControllerApi {
    /**
     * 查询课程计划
     * @param courseId 课程id
     * @return 课程计划对象
     */
    @ApiOperation("课程计划查询")
    TeachplanNode findTeachPlanList(String courseId);

    /**
     *
     * @param teachplan
     * @return
     */
    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);

    /**
     * 课程列表查询
     * @param page 当前页码
     * @param size 每页记录数
     * @param courseListRequest 条件查询对象
     * @return 查询响应结果
     */
    @ApiOperation("查询课程列表")
    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    /**
     * 添加课程
     * @param courseBase 课程对象
     * @return 相应结果
     */
    @ApiOperation("添加课程")
    AddCourseResult addCourseBase(CourseBase courseBase);

    /**
     * 查询课程信息
     * @param courseId 课程id
     * @return 课程对象
     */
    @ApiOperation("查询课程信息")
    CourseBase courseView(String courseId);

    /**
     * 修改课程信息
     * @param courseId 课程id
     * @param courseBase 修改的课程信息
     * @return 带有课程id的响应结果
     */
    @ApiOperation("修改课程信息")
    AddCourseResult courseBaseUpdate(String courseId,CourseBase courseBase);

    /**
     * 根据课程id查询课程营销你
     * @param courseId 课程id
     * @return 相应结果对象
     */
    @ApiOperation("查询课程营销")
    CourseMarket getCourseMarket(String courseId);

    /**
     * 修改课程营销
     * @param courseId 课程id
     * @param courseMarket 课程营销对象
     * @return 相应结果
     */
    @ApiOperation("修改课程营销")
    ResponseResult updateCourseMarket(String courseId,CourseMarket courseMarket);

    /**
     * 添加课程图片
     * @param courseId 课程id
     * @param pic 图片id
     * @return 响应对象
     */
    @ApiOperation("添加课程图片")
    ResponseResult addCoursePic(String courseId,String pic);

    /**
     * 根据课程id 查询图片
     * @param courseId 课程id
     * @return 课程图片
     */
    @ApiOperation("查询课程图片集合")
    CoursePic findCoursePicList(String courseId);

    /**
     *删除课程图片数据
     * @param courseId 课程id
     * @return 响应结果
     */
    @ApiOperation("删除课程图片")
    ResponseResult deleteCoursePic(String courseId);

    /**
     * 获得课程详情中的数据
     * @param courseId 课程id
     * @return 课程视图
     */
    @ApiOperation("课程视图查询")
    CourseView getCourseView(String courseId);

    /**
     *课程预览
     * @param courseId 课程id
     * @return 课程发布结果
     */
    @ApiOperation("课程预览")
    CoursePublishResult coursePreview(String courseId);

    /**
     *课程发布
     * @param courseId 课程id
     * @return 课程发布结果
     */
    @ApiOperation("课程发布")
    CoursePublishResult coursePublish(String courseId);

    /**
     * 将课程计划和媒资文件关联对象保存
     * @param teachplanMedia 课程计划和媒资文件关联对象
     * @return 相应结果
     */
    @ApiOperation("保存媒资信息")
    ResponseResult saveTeachPlanMedia(TeachplanMedia teachplanMedia);
}
