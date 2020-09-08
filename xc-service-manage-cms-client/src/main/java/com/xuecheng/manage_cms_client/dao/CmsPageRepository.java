package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author 1159588554@qq.com
 * @date 2020/6/11 17:40
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    /**
     * 根据页面名称查询
     * @param pageName 页面名字
     * @return 页面对象
     */
    CmsPage findByPageName(String pageName);

    /**
     * 根据页面名称、站点Id、页面webpath查询
     * @param pageName 页面名称
     * @param siteId 站点id
     * @param pageWebPath 页面路径
     * @return 页面对象
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId, String pageWebPath);
}
