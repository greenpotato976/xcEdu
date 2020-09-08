package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/1 18:26
 */
@Data
public class CourseView implements Serializable {
    //课程基本信息
    CourseBase courseBase;
    //课程营销信息
    CourseMarket courseMarket;
    //课程图片
    CoursePic coursePic;
    //课程营销计划
    TeachplanNode teachplanNode;
}
