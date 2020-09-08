package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/28 14:16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsConfigServiceTest {
    @Autowired
    CmsConfigService cmsConfigService;

    @Test
    public void testGetHtml(){
        String pageHtml = cmsConfigService.getPageHtml("5e034cf73cf44b42441592ba");
        System.out.println(pageHtml);
    }
}
