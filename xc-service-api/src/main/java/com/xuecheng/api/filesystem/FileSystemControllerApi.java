package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 1159588554@qq.com
 * @date 2020/7/22 11:38
 */
@Api(value="文件管理接口",description = "文件管理接口，提供文件的增、删、改、查")
public interface FileSystemControllerApi {
    /**
     * 上传文件
     * @param multipartFile 需要上传的文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param metadata 文件员信息
     * @return 包含操作信息的文件对象
     */
    UploadFileResult uploadFile(MultipartFile multipartFile,String filetag, String businesskey, String metadata);
}
