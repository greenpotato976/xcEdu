package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/24 16:26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseBaseMapperTest {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    public void testFindlist(){
        PageHelper.startPage(1,10);
        CourseListRequest courseListRequest = new CourseListRequest();
        Page<CourseInfo> courseInfos = courseBaseMapper.findList(courseListRequest);
        List<CourseInfo> courseInfoList = courseInfos.getResult();
        System.out.println(courseInfoList);
    }
}
