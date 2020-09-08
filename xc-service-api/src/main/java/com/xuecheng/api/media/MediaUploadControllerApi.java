package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/14 18:00
 */
@Api(value = "媒资管理接口", description = "媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {
    /**
     *向服务端请求注册上传文件
     * @param fileMd5 文件唯一表示
     * @param fileName 文件名字
     * @param fileSize 文件大小
     * @param mimeType 文件类型
     * @param fileExt 文件扩展名
     * @return 相应结果
     */
    @ApiOperation("文件上传注册")
    ResponseResult register(String fileMd5,
                            String fileName,
                            Long fileSize,
                            String mimeType,
                            String fileExt);

    /**
     *每次上传分块前校验分块，如果已存在分块则不再上传，达到断点续传的目的
     * @param fileMd5 文件唯一表示
     * @param chunk 当前分块下标
     * @param chunkSize 当前分块大小
     * @return 相应结果
     */
    @ApiOperation("分块检查")
    CheckChunkResult checkChunk(String fileMd5,
                                Integer chunk,
                                Integer chunkSize);

    /**
     * 上传文件
     * @param file 上传的分块
     * @param chunk 当前分块的下标
     * @param fileMd5 文件唯一表示
     * @return 相应结果
     */
    @ApiOperation("上传分块")
    ResponseResult uploadChunk(MultipartFile file,
                                      Integer chunk,
                                      String fileMd5);
    /**
     *合并分块
     * @param fileMd5 文件唯一表示
     * @param fileName 文件名字
     * @param fileSize 文件大小
     * @param mimeType 文件类型
     * @param fileExt 文件扩展名
     * @return 相应结果
     */
    @ApiOperation("合并文件")
    ResponseResult mergeChunks(String fileMd5,
                               String fileName,
                               Long fileSize,
                               String mimeType,
                               String fileExt);
}
