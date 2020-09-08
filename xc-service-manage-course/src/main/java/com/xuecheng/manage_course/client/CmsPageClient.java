package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/1 12:38
 * 远程调用cmsPage接口
 */

@FeignClient(value = "XC-SERVICE-MANAGE-CMS")
public interface CmsPageClient {
    @GetMapping("/cms/page/get/{pageId}")
    CmsPage findById(@PathVariable("pageId") String pageId);

    @PostMapping("/cms/page/save")
    CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);

    @PostMapping("/cms/page/quickPost")
    CmsPostPageResult quickPost(@RequestBody CmsPage cmsPage);
}
