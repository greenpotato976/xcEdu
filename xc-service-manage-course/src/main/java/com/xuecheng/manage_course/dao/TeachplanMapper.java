package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/22 14:27
 */
@Mapper
public interface TeachplanMapper {
    TeachplanNode selectList(String courseId);
}
