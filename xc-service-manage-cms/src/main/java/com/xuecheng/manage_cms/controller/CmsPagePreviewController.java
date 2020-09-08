package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-15 16:20
 **/
@Controller
@RequestMapping("/cms/config")
public class CmsPagePreviewController extends BaseController {

    @Autowired
    CmsConfigService cmsConfigService;

    //页面预览
    @GetMapping("/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        //执行静态化
        String pageHtml = cmsConfigService.getPageHtml(pageId);
        //通过response对象将内容输出
        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("Content-type","text/html;charset=utf-8");
        outputStream.write(pageHtml.getBytes(StandardCharsets.UTF_8));
    }
}
