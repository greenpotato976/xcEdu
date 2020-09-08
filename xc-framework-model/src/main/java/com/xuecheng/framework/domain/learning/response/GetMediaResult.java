package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/20 20:10
 */
@Data
@NoArgsConstructor
@ToString
public class GetMediaResult extends ResponseResult {
    public GetMediaResult(ResultCode resultCode, String fileUrl) {
        super(resultCode);
        this.fileUrl = fileUrl;
    }
    //媒资文件播放地址
    private String fileUrl;
}
