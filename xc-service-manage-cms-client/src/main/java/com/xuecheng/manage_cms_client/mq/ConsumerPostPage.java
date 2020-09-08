package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发布页面的消费客户端，监听 页面发布队列的消息，收到消息后从mongodb下载文件，保存在本地。
 * 消息格式
 * { "pageId":"" }
 * @author 1159588554@qq.com
 * @date 2020/6/12 11:18
 */
@Component
public class ConsumerPostPage {
    private static  final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    PageService pageService;
    /**
     * 监听页面发布方法
     * @param msg 消息
     */
    @RabbitListener(queues={"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        //解析消息
        Map msgMap = JSON.parseObject(msg, Map.class);
        //得到pageId
        String pageId = (String) msgMap.get("pageId");
        //校验页面是否合法
        CmsPage cmsPage = pageService.findCmsPageById(pageId);
        if(cmsPage == null){
            LOGGER.error("receive postpage msg,cmsPage is null,pageId:{}",pageId);
            return;
        }
        //调用保存方法
        pageService.savePageToServerPath(pageId);
    }
}
