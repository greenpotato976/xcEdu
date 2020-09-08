package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/24 19:09
 */
@Controller
@RequestMapping("/cms/config")
public class CmsConfigController extends BaseController implements CmsConfigControllerApi {
    @Autowired
    CmsConfigService cmsConfigService;

    /**
     *查询cms配置对象
     * @param id 主键id
     * @return cmsConfig对象
     */
    @Override
    @ResponseBody
    @GetMapping("/getModel/{id}")
    public CmsConfig getModel(@PathVariable("id") String id) {
        return cmsConfigService.getCmsConfigById(id);
    }

}
