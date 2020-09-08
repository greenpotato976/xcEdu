package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * @author itcast
 */
@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    /**
     * 查询所有
     * 接口APi
     * @param page 当前页
     * @param size 每页显示
     * @param queryPageRequest 条件查询的条件
     * @return json
     */
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) ;

    /**
     * 添加页面接口
     * @param cmsPage 需要添加页面
     * @return 添加成功的页面
     */
    @ApiOperation("页面添加")
    CmsPageResult add(CmsPage cmsPage);

    /**
     * 保存页面如果存在就更新
     * @param cmsPage 需要添加页面
     * @return 添加成功的页面
     */
    @ApiOperation("页面保存")
    CmsPageResult saveCmsPage(CmsPage cmsPage);

    /**
     * 根据id查询页面对象
     * @param id 主键id
     * @return 页面对象
     */
    @ApiOperation("根据id查询页面对象")
    CmsPage findById(String id);

    /**
     * 编辑页面信息
     * @param id 主键id
     * @param cmsPage 页面json
     * @return 页面操作结果
     */
    @ApiOperation("修改页面信息")
    CmsPageResult edit(String id,CmsPage cmsPage);

    /**
     * 删除页面
     * @param id 主键id
     * @return 响应结果
     */
    @ApiOperation("删除页面")
    ResponseResult delete(String id);

    /**
     * 发布页面
     * @param pageId 页面id
     * @return 响应结果
     */
    @ApiOperation("发布页面")
    ResponseResult post(String pageId);

    /**
     * 一键发布页面
     * @param cmsPage 页面
     * @return 页面发布结果
     */
    @ApiOperation("一键发布页面")
    CmsPostPageResult quickPost(CmsPage cmsPage);
}
