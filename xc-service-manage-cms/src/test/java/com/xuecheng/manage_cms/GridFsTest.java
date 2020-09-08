package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * GridFs测试类
 * @author 1159588554@qq.com
 * @date 2020/5/27 18:55
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;

    /**
     * GridFs存文件
     * @throws FileNotFoundException
     */
    @Test
    public void addFile() throws FileNotFoundException {
        File file = new File("G:\\course.ftl");
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectId objectId = gridFsTemplate.store(fileInputStream, "课程详情模板");
        System.out.println(objectId);
    }

    /**
     * 取文件测试
     */
    @Test
    public void getFile() throws IOException {
        //根据id查询文件
        GridFSFile fsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5ecf3cf925e69527a0efcef8")));
        //打开一个下载流对象
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());
        //创建GridFsResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(fsFile,downloadStream);
        //从流中取数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }
}
