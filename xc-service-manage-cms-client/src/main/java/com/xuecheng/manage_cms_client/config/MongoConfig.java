package com.xuecheng.manage_cms_client.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MongoDB的配置类
 * @author 1159588554@qq.com
 * @date 2020/6/11 17:09
 */
@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.database}")
    String db;

    /**
     *获取下载流
     * @param mongoClient mongo客户端
     * @return GridFSBucket
     */
    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient){
        MongoDatabase database = mongoClient.getDatabase(db);
        return GridFSBuckets.create(database);
    }
}
