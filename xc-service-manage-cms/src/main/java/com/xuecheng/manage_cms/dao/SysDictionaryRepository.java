package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/26 18:30
 */
public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {
    SysDictionary findBydType(String dType);
}
