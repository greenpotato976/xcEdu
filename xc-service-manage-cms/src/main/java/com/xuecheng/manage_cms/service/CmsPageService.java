package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author 1159588554@qq.com
 */
@Service
public class CmsPageService {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsConfigService cmsConfigService;
    @Autowired
    CmsSiteRepository cmsSiteRepository;

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        //对查询条件对象判断空
        if(queryPageRequest == null){
            queryPageRequest = new QueryPageRequest();
        }
        //对查询条件封装
        CmsPage cmsPage = new CmsPage();
        //设置PageAliase
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //设置pageId
        if(StringUtils.isNotEmpty(queryPageRequest.getPageId())){
            cmsPage.setPageId(queryPageRequest.getPageId());
        }
        //设置name
        if(StringUtils.isNotEmpty(queryPageRequest.getPageName())){
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        //设置siteId
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置templateId
        if(StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }

        //准备匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> cmsPageExample = Example.of(cmsPage,exampleMatcher);
        //对参数处理
        if(page <= 0){
            page = 1;
        }
        page--;
        if(size <= 0){
            size = 10;
        }
        //分页对象
        PageRequest pageable = PageRequest.of(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(cmsPageExample,pageable);
        //对查询结果封装
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }

    /**
     * 添加方法
     * @param cmsPage 需要添加的页面对象
     * @return 页面结果
     */
    public CmsPageResult add(CmsPage cmsPage) {
        //异常抛出
        //参数不合法
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //页面已经存在
        if(cmsPage1 != null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //调用dao更新操作
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }

    /**
     * 修改数据时，需要回显数据
     * @param id 主键
     * @return 页面对象
     */
    public CmsPage findById(String id) {
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        if(cmsPage.isPresent()){
            return cmsPage.get();
        }
        return null;
    }

    /**
     * 编辑页面信息
     * @param id 页面主键
     * @param cmsPage 页面对象
     * @return 修改操作信息
     */
    public CmsPageResult edit(String id, CmsPage cmsPage) {
        //先根据主键查询
        CmsPage one = this.findById(id);
        if(one != null){
            //设置修改的值
            //设置页面名字
            one.setPageName(cmsPage.getPageName());
            //设置页面id
            one.setTemplateId(cmsPage.getTemplateId());
            //设置别名
            one.setPageAliase(cmsPage.getPageAliase());
            //访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //设置DataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //保存对象
            CmsPage save = cmsPageRepository.save(one);
            if(save != null){
                //操作成功
                return new CmsPageResult(CommonCode.SUCCESS,one);
            }
        }
        //操作失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    public ResponseResult delete(String id) {
        //根据id查询
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            //不为空删除
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    public CmsPageResult save(CmsPage cmsPage) {
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPage cmsPageExist = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPageExist != null){
            //数据库中存在
            return this.edit(cmsPageExist.getPageId(),cmsPageExist);
        } else {
            //数据库中不存在
            return this.add(cmsPage);
        }
    }

    /**
     * 一键发布功能
     * @param cmsPage 页面
     * @return 页面发布结果
     */
    public CmsPostPageResult quickPost(CmsPage cmsPage) {
        //参数校验
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //添加页面，这里直接调用我们在做预览页面时候开发的保存页面方法
        CmsPageResult save = this.save(cmsPage);
        if(!save.isSuccess()){
            //失败
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //发布页面,通知cms client发布页面
        String pageId = save.getCmsPage().getPageId();
        ResponseResult responseResult = cmsConfigService.postPage(pageId);
        if(!responseResult.isSuccess()){
            //失败
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //得到页面的url，页面url=站点域名+站点webpath+页面webpath+页面名称
        //站点域名
        String siteId = cmsPage.getSiteId();
        Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(siteId);
        if(!cmsSiteOptional.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsSite cmsSite = cmsSiteOptional.get();
        String postUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + cmsPage.getPageWebPath() + cmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,postUrl);
    }
}
