package com.xuecheng.learning.client;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/20 20:16
 */
@FeignClient("XC-SERVICE-SEARCH")
public interface CourseSearchClient {
    /**
     * 调用搜索程序进行搜索
     * @param teachplanId 课程计划id
     * @return 课程计划媒资关联对象
     */
    @GetMapping("/search/course/getMedia/{teachplanId}")
    TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);
}
