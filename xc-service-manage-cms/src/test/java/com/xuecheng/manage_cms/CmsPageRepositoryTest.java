package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    /**
     * 测试cmsPage文档查询
     */
    @Test
    public void testFindAll(){
        List<CmsPage> cmsPages = cmsPageRepository.findAll();
        System.out.println(cmsPages);
    }

    /**
     * 测试cmsPage文档查询
     */
    @Test
    public void testFindPage(){
        Pageable pageable = PageRequest.of(0,10);
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(pageable);
        System.out.println(cmsPages);
    }

    /**
     * 测试cmsPage文档添加
     */
    @Test
    public void testInsert(){
        //添加一条记录
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试添加页面1");
        CmsPage cmsPage1 = cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage1);
    }

    /**
     * 测试cmsPageRepository自定义查询方法
     */
    @Test
    public void testFindByPageName(){
        CmsPage cmsPage1 = cmsPageRepository.findByPageName("测试添加页面1");
        System.out.println(cmsPage1);
    }

    /**
     * 测试cmsPageRepository删除
     */
    @Test
    public void testDelete(){
        cmsPageRepository.deleteById("5eae7a4ef6101e4fbccdb953");
    }

    /**
     * 测试cmsPageRepository修改
     */
    @Test
    public void testUpdate(){
        //先查询要修改的对象
        Optional<CmsPage> optional= cmsPageRepository.findById("5eae7a4ef6101e4fbccdb953");
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            //修改对象
            cmsPage.setPageName("测试页面2");
            //提交修改
            cmsPageRepository.save(cmsPage);
        }
    }

    /**
     * 测试条件查询
     */
    @Test
    public void testCondition(){
        //准备pageable
        Pageable pageable = PageRequest.of(1,10);
        //准备Example
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageAliase("轮播图");
        //准备匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> cmsPageExample = Example.of(cmsPage,exampleMatcher);

        Page<CmsPage> all = cmsPageRepository.findAll(cmsPageExample, pageable);
        List<CmsPage> cmsPages = all.getContent();
        System.out.println(cmsPage);
    }
}
