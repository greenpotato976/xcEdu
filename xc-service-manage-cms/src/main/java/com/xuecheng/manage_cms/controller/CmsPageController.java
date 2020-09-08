package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsConfigService;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.bouncycastle.cms.CMSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 1159588554@qq.com
 *
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    CmsPageService cmsPageService;

    @Autowired
    CmsConfigService cmsConfigService;
    /**
     * 查询所有cms_page对象
     * @param page 当前页码
     * @param size 每页条目数
     * @param queryPageRequest 条件查询
     * @return 标准json对象
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        return cmsPageService.findList(page,size,queryPageRequest);
    }

    /**
     * 添加页面方法
     * @param cmsPage 需要添加页面
     * @return 页面响应实体对象
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return cmsPageService.add(cmsPage);
    }

    /**
     * 保存页面
     * @param cmsPage 需要保存的页面
     * @return 页面响应实体对象
     */
    @Override
    @PostMapping("/save")
    public CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage) {
        return cmsPageService.save(cmsPage);
    }

    /**
     * 根据主键id查询页面
     * @param id 主键id
     * @return 页面
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return cmsPageService.findById(id);
    }

    /**
     * 编辑修改页面
     * put请求表示更新
     * @param id 主键id
     * @param cmsPage 页面json
     * @return 页面操作对象
     */
    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id") String id,@RequestBody CmsPage cmsPage) {
        return cmsPageService.edit(id,cmsPage);
    }

    /**
     * 删除页面
     * @param id 主键id
     * @return 相应结果
     */
    @DeleteMapping("/delete/{id}")
    @Override
    public ResponseResult delete(@PathVariable("id") String id) {
        return cmsPageService.delete(id);
    }

    /**
     * 页面发布方法
     * @param pageId 页面id
     * @return 相应结果
     */
    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return cmsConfigService.postPage(pageId);
    }

    @Override
    @PostMapping("/quickPost")
    public CmsPostPageResult quickPost(@RequestBody CmsPage cmsPage) {
        return cmsPageService.quickPost(cmsPage);
    }
}
