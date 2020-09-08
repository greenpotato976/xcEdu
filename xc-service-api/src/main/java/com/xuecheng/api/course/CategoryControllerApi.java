package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 17:20
 */
@Api(value = "课程分类管理",description = "课程分类管理",tags = {"课程分类管理"})
public interface CategoryControllerApi {
    /**
     *查询目录列表
     * @return 目录对象
     */
    @ApiOperation("查询目录")
    CategoryNode findList();
}
