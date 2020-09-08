package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/23 18:35
 */
public interface TeachplanRepository extends JpaRepository<Teachplan,String> {
    List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
