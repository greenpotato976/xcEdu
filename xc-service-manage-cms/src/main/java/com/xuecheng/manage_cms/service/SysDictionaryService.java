package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 18:34
 */
@Service
public class SysDictionaryService {
    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    public SysDictionary findByType(String type){
        return sysDictionaryRepository.findBydType(type);
    }
}
