package com.xuecheng.api.lean;

import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import io.swagger.annotations.Api;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/20 20:09
 */
@Api(value = "录播课程学习管理",description = "录播课程学习管理")
public interface CourseLearningControllerApi {
    /**
     * 根据课程计划id查询出视频地址
     * @param courseId 课程id
     * @param teachPlanId 课程计划id
     * @return 包含视频地址的相应结果
     */
    GetMediaResult getMediaPlayUrl(String courseId,String teachPlanId);
}
