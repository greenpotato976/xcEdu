package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;
import java.io.*;
import java.util.Optional;

/**
 * 页面服务类
 * @author 1159588554@qq.com
 * @date 2020/6/11 17:39
 */
@Service
public class PageService {
    private static  final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;
    /**
     * 保存html页面到服务器物理路径
     * 页面物理路径=站点物理路径+页面物理路径+页面名称
     * @param pageId 页面id
     */
    public void savePageToServerPath(String pageId){
        //获得页面对象
        CmsPage cmsPage = this.findCmsPageById(pageId);
        //拿到页面文件id
        String fileId = cmsPage.getHtmlFileId();
        //得到文件流
        InputStream inputStream = this.getFileById(fileId);
        if(inputStream == null){
            LOGGER.error("getFileById InputStream is null ,htmlFileId:{}",fileId);
            return;
        }
        //获得站点对象
        String siteId = cmsPage.getSiteId();
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //拿到站点物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //得到页面的物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //获取输出流
        FileOutputStream fileOutputStream = null;
        //拷贝
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据文件id从GridFS中查询文件内容
     * @param fileId 文件id
     * @return 文件输入流
     */
    public InputStream getFileById(String fileId){
        //获得文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //获得下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFSResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据id查询CmsPage
     * @param pageId 主键id
     * @return 页面对象
     */
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        return optional.orElse(null);
    }

    /**
     * 根据id查找站点对象
     * @param cmsSiteId 站点id
     * @return 站点对象
     */
    public CmsSite findCmsSiteById(String cmsSiteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(cmsSiteId);
        return optional.orElse(null);
    }
}
