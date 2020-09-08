package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/24 19:04
 */
@Api(value="cms配置管理接口",description = "cms配置管理接口，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi {
    /**
     * 根据id查询CMS配置信息
     * @param id 主键id
     * @return cms配置信息
     */
    @ApiOperation("根据id查询CMS配置信息")
    CmsConfig getModel(String id);


}
