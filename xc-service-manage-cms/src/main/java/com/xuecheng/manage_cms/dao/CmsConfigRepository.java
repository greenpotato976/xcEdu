package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * cms配置持久化接口
 * @author 1159588554@qq.com
 * @date 2020/5/24 19:10
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
