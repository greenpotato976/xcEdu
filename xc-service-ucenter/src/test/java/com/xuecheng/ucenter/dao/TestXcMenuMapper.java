package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/24 17:06
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestXcMenuMapper {
    @Autowired
    XcMenuMapper xcMenuMapper;

    @Test
    public void testSelectPermissionByUserId(){
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId("49");
        System.out.println(xcMenus);
    }
}
