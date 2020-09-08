package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.util.Optional;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/27 11:11
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseMarketDaoTest {
    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Test
    public void getById(){
        Optional<CourseMarket> optional = courseMarketRepository.findById("402885816243d2dd016243f24c030002");
        CourseMarket courseMarket = optional.get();
        System.out.println(courseMarket);
    }
}
