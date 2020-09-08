package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 异常抛出工具类
 * @author 1159588554@qq.com
 * @date 2020/5/22 10:34
 */
public class ExceptionCast {
    /**
     * 静态方法抛出自定义异常
     * @param resultCode 结果码
     */
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
