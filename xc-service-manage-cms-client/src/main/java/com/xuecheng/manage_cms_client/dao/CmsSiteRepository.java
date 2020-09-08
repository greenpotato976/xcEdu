package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * CmsSite持久层
 * @author 1159588554@qq.com
 * @date 2020/6/11 17:47
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
