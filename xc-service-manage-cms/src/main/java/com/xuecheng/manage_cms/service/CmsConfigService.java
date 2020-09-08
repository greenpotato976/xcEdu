package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/24 19:11
 */
@Service
public class CmsConfigService {
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    CmsPageService cmsPageService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 根据id查询CmsConfig
     * @param id 主键id
     * @return cmsConfig
     */
    public CmsConfig getCmsConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     *静态化程序获取页面的DataUrl
     *
     *静态化程序远程请求DataUrl获取数据模型。
     *
     *静态化程序获取页面的模板信息
     *
     *执行页面静态化
     * @param pageId 页面id
     * @return 静态化页面
     */
    public String getPageHtml(String pageId){
        //获得数据模型
        Map model = getModelByPageId(pageId);
        if(model == null){
            //模板数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获得模板内容
        String template = getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(pageId)){
            //模板内容为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = generateHtml(template, model);
        return html;
    }


    /**
     * 执行静态化
     * @param templateContent 模板信息
     * @param model 数据模型
     * @return 静态化页面信息
     */
    public String generateHtml(String templateContent,Map model){
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 通过pageId查询模板信息
     * @param pageId 主键id
     * @return 模板信息
     */
    public String getTemplateByPageId(String pageId){
        //取出页面对象
        CmsPage cmsPage = cmsPageService.findById(pageId);
        if(cmsPage == null){
            //抛出页面不存在异常
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出模板id
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if(optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFS中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 通过pageId查询数据模型
     * @param pageId 主键id
     * @return 数据模型
     */
    public Map getModelByPageId(String pageId){
        //取出页面信息
        CmsPage cmsPage = cmsPageService.findById(pageId);
        if(cmsPage == null){
            //抛出页面找不到异常
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出数据模型url
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            //从页面信息中找不到获取数据的url
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * 发布页面
     * @param pageId 页面id
     * @return 相应结果
     */
    public ResponseResult postPage(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        if(StringUtils.isEmpty(pageHtml)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //向GridFS中添加文件
        CmsPage cmsPage = this.saveHtml(pageHtml,pageId);
        //发送消息
        this.sendPostPageMessage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 将文件保存到GridFS中
     * @param pageHtml 页面代码
     * @param pageId 页面id
     * @return 保存成功的页面对象
     */
    public CmsPage saveHtml(String pageHtml,String pageId){
        CmsPage cmsPage = cmsPageService.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //将页面内容转成输入流
        ObjectId objectId = null;
        try {
            InputStream inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            //保存到GridFS
            objectId = gridFsTemplate.store(inputStream,cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将id更新至cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        return cmsPageRepository.save(cmsPage);
    }

    /**
     * 向mq发送消息
     * @param pageId 页面id
     */
    public void sendPostPageMessage(String pageId){
        //判断参数合法性
        CmsPage cmsPage = cmsPageService.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //构造消息
        Map<String,String> msgMap = new HashMap<String,String>();
        msgMap.put("pageId",pageId);
        String msg = JSON.toJSONString(msgMap);
        //发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),msg);
    }
}
