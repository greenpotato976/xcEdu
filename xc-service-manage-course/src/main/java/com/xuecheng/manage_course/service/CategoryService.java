package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 17:57
 */
@Service
public class CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    public CategoryNode findList(){
        return categoryMapper.findList();
    }
}
