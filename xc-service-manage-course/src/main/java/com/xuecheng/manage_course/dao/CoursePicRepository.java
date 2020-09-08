package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/7/23 21:17
 */
public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
    /**
     * 根据课程id 删除课程图片
     * @param courseid 课程id
     * @return 成功返回1
     */
    Long deleteByCourseid(String courseid);
}
