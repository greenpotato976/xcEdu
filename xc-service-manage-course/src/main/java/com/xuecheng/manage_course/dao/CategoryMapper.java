package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 17:42
 */
@Mapper
public interface CategoryMapper {
    /**
     * 查询目录列表
     * @return 目录对象
     */
    CategoryNode findList();
}
