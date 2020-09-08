package com.xuecheng.auth.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 17:21
 */
@FeignClient(XcServiceList.XC_SERVICE_UCENTER)
public interface UcenterClient {
    /**
     * 远程调用用户服务获得用户信息
     * @param Username 用户名
     * @return 用户扩展对象
     */
    @GetMapping("/ucenter/getuserext")
    XcUserExt getUserExt(@RequestParam("username") String Username);
}
