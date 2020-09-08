package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


/**
 * @author 1159588554@qq.com
 * @date 2020/7/22 11:51
 */
@Service
public class FileSystemService {
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;

    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param metadata 文件元信息
     * @return 包含操作信息的文件对象
     */
    public UploadFileResult uploadFile(MultipartFile multipartFile,String filetag, String businesskey, String metadata){
        //对参数进行校验
        if(multipartFile == null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //对参数进行处理
        Map metadataMap = null;
        if(!StringUtils.isEmpty(metadata)){
            //不为空,转换为map
            metadataMap = JSON.parseObject(metadata, Map.class);
        }
        //1.上传至FastDfs
        String fileId = uploadToFastDfs(multipartFile);
        //2.保存至服务器
        FileSystem fileSystem = new FileSystem();
        //设置属性
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFiletag(filetag);
        fileSystem.setMetadata(metadataMap);
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }


    /**
     * 将文件上传至FastDfs
     * @param multipartFile 文件
     * @return 文件id
     */
    private String uploadToFastDfs(MultipartFile multipartFile){
        try {
            //初始化fastDFS的环境
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建trackerClient
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storage服务器
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient来上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //上传文件
            //得到文件字节
            byte[] bytes = multipartFile.getBytes();
            //得到文件的原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //得到文件扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, ext, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
