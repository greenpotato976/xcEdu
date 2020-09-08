package com.xuecheng.test.freemarker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/26 12:48
 */
@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/banner")
    public String indexBanner(Map<String,Object> map){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/page/getModel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
    }

    @RequestMapping("/test01")
    public String freemarker(Map<String,Object> map){
        map.put("name","admin");
        //返回模板
        return "test01";
    }

    @RequestMapping("/course")
    public String course(Map<String,Object> map){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31200/course/getCourseView/4028e581617f945f01617f9dabc40000", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "course";
    }
}
