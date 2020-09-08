package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/22 15:39
 */
@RequestMapping("/course")
@RestController
public class CourseController extends BaseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;

    @PreAuthorize("hasAuthority('course_teachplan_list')")
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanList(@PathVariable("courseId") String courseId) {
        return courseService.selectList(courseId);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.teachPlanAdd(teachplan);
    }

    @PreAuthorize("hasAuthority('course_find_list')")
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        //查询用户企业id
        //调用工具类取出用户信息
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        //从用户header中附带的jwt令牌取出用户信息
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        return courseService.findCourseList(userJwt.getCompanyId(),page, size, courseListRequest);
    }

    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }


    @Override
    @GetMapping("/getCourseBase/{courseId}")
    public CourseBase courseView(@PathVariable("courseId") String courseId) {
        return courseService.courseView(courseId);
    }

    @Override
    @PutMapping("/update/{courseId}")
    public AddCourseResult courseBaseUpdate(@PathVariable("courseId") String courseId,@RequestBody CourseBase courseBase) {
        return courseService.update(courseId, courseBase);
    }

    @Override
    @GetMapping("/courseMarket/get/{courseId}")
    public CourseMarket getCourseMarket(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    @Override
    @PutMapping("/courseMarket/update/{courseId}")
    public ResponseResult updateCourseMarket(@PathVariable("courseId") String courseId,@RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(courseId, courseMarket);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId, pic);
    }


    @PreAuthorize("hasAuthority('course_find_pic')")
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePicList(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePicList(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/getCourseView/{courseId}")
    public CourseView getCourseView(@PathVariable("courseId") String courseId) {
        return courseService.getCourseview(courseId);
    }

    @Override
    @PostMapping("/preview/{courseId}")
    public CoursePublishResult coursePreview(@PathVariable("courseId") String courseId) {
        return courseService.preview(courseId);
    }

    @Override
    @PostMapping("/publish/{courseId}")
    public CoursePublishResult coursePublish(@PathVariable("courseId") String courseId) {
        return courseService.coursePublish(courseId);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult saveTeachPlanMedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.saveTeachPlanMedia(teachplanMedia);
    }
}
