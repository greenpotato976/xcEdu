package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 17:02
 */
@Api(value = "用户中心",description = "用户中心管理")
public interface UcenterControllerApi {

    @ApiOperation("获取用户信息")
    XcUserExt getUserExt(String username);
}
