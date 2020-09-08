package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/24 19:41
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HttpTemplateTest {
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testHttpTemplateTest(){
        ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:31001/cms/page/getModel/5a791725dd573c3574ee333f", Map.class);
        System.out.println(entity);
    }
}
