package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 17:55
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryMapperTest {
    @Autowired
    CategoryMapper categoryMapper;

    @Test
    public void testFindList(){
        CategoryNode categoryNode = categoryMapper.findList();
        System.out.println(categoryNode);
    }
}
