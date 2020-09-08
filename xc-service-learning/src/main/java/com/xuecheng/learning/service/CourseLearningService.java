package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/20 20:24
 */
@Service
public class CourseLearningService {
    @Autowired
    CourseSearchClient courseSearchClient;
    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;


    /**
     * 根据课程计划查询视频地址
     * @param courseId 课程id
     * @param teachPlanId 课程计划id
     * @return 包含视频地址
     */
    public GetMediaResult getMedia(String courseId, String teachPlanId) {
        //参数校验
        if(courseId == null || teachPlanId == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //检验学生付款情况

        //调用外部接口
        TeachplanMediaPub TeachplanMediaPub = courseSearchClient.getmedia(teachPlanId);
        if(TeachplanMediaPub == null){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        return new GetMediaResult(CommonCode.SUCCESS,TeachplanMediaPub.getMediaUrl());
    }

    /**
     * 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @param valid 有效期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param xcTask 任务对象
     * @return 相应结果
     */
    @Transactional
    public ResponseResult addCourse(String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask){
        //参数校验
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(LearningCode.LEARNING_GET_MEDIA_ERROR);
        }
        if (StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULl);
        }
        if(xcTask == null || StringUtils.isEmpty(xcTask.getId())){
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }
        //查询出选课对象
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        //判断是否为空
        if(xcLearningCourse != null){
            //不为空
            //修改选课对象
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        } else {
            //没有记录添加新的
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }
        //向历史任务表播入记录
        Optional<XcTaskHis> XcTaskHIsOptional = xcTaskHisRepository.findById(xcTask.getId());
        if(!XcTaskHIsOptional.isPresent()){
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
