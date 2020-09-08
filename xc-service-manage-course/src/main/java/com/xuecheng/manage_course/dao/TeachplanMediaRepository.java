package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/23 18:35
 */
public interface TeachplanMediaRepository extends JpaRepository<TeachplanMedia,String> {
    List<TeachplanMedia> findByCourseId(String courseId);
}
