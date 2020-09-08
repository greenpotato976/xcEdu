package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LombokTest {

    @Test
    public void testGS(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("test01");
        System.out.println(cmsPage);
    }
}
