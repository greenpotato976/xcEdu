package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/7/22 11:50
 */
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
