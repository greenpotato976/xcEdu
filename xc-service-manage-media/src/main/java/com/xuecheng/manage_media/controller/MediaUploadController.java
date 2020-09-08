package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaUploadControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/14 18:30
 */
@RestController
@RequestMapping("/media/upload")
public class MediaUploadController implements MediaUploadControllerApi {
    @Autowired
    MediaUploadService mediaUploadService;

    /**
     *向服务端请求注册上传文件
     * @param fileMd5 文件唯一表示
     * @param fileName 文件名字
     * @param fileSize 文件大小
     * @param mimeType 文件类型
     * @param fileExt 文件扩展名
     * @return 相应结果
     */
    @Override
    @PostMapping("/register")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {
        return mediaUploadService.register(fileMd5,fileName,fileSize,mimeType,fileExt);
    }
    /**
     *每次上传分块前校验分块，如果已存在分块则不再上传，达到断点续传的目的
     * @param fileMd5 文件唯一表示
     * @param chunk 当前分块下标
     * @param chunkSize 当前分块大小
     * @return 相应结果
     */
    @Override
    @PostMapping("/checkchunk")
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
        return mediaUploadService.checkChunk(fileMd5,chunk,chunkSize);
    }
    /**
     * 上传文件
     * @param file 上传的分块
     * @param chunk 当前分块的下标
     * @param fileMd5 文件唯一表示
     * @return 相应结果
     */
    @Override
    @PostMapping("/uploadchunk")
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5) {
        return mediaUploadService.uploadChunk(file,chunk,fileMd5);
    }
    /**
     *合并分块
     * @param fileMd5 文件唯一表示
     @param fileName 文件名字
      * @param fileSize 文件大小
     * @param mimeType 文件类型
     * @param fileExt 文件扩展名
     * @return 相应结果
     */
    @Override
    @PostMapping("/mergechunks")
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {
        return mediaUploadService.mergeChunks(fileMd5,fileName,fileSize,mimeType,fileExt);
    }
}
