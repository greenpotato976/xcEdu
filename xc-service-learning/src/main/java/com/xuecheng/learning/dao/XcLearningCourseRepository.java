package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/26 16:29
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse,String> {
    /**
     * 根据用户id和课程id 查询对象
     * @param userId 用户id
     * @param courseId 课程id
     * @return 查询对象
     */
    XcLearningCourse findByUserIdAndCourseId(String userId,String courseId);
}
