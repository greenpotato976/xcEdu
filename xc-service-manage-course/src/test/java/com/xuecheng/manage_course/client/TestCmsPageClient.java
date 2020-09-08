package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/1 12:47
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCmsPageClient {
    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void testFindById(){
        CmsPage cmsPage = cmsPageClient.findById("5a795ac7dd573c04508f3a56");
        System.out.println(cmsPage);
    }
}
