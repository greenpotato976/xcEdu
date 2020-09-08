package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 18:24
 */
@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDictionaryControllerApi {
    /**
     * 根据type类型查询数据字典
     * @param type 字典类型
     * @return 系统数据字典
     */
    @ApiOperation(value="数据字典查询接口")
    SysDictionary getByType(String type);
}
