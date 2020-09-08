package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * cmsPage文档的持久化接口
 * @author 115959588554@qq.com
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    /**
     * 根据页面名称查询 页面信息
     * @param pageName 页面名称
     * @return 查询成功的页面
     */
    CmsPage findByPageName(String pageName);

    /**
     * 根据索引查询页面
     *
     * @param pageName 页面名字
     * @param siteId 站点ID
     * @param pageWebPath 页面路径
     * @return 页面对象
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}
