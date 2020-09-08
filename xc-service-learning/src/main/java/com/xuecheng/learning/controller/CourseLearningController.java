package com.xuecheng.learning.controller;

import com.xuecheng.api.lean.CourseLearningControllerApi;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.learning.service.CourseLearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/20 20:23
 */
@RestController
@RequestMapping("/learning/course")
public class CourseLearningController implements CourseLearningControllerApi {
    @Autowired
    CourseLearningService courseLearningService;

    @Override
    @GetMapping("/getmedia/{courseId}/{teachplanId}")
    public GetMediaResult getMediaPlayUrl(@PathVariable("courseId") String courseId,@PathVariable("teachplanId") String teachPlanId) {
        return courseLearningService.getMedia(courseId,teachPlanId);
    }
}
