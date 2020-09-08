package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.domain.media.response.MediaFileResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/17 19:35
 */
@Api(value = "媒体文件管理",description = "媒体文件管理接口",tags = {"媒体文件管理接口"})
public interface MediaFileControllerApi {
    QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest);
}
