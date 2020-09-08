package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/28 13:54
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
