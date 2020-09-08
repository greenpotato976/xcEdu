package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
/**
 * 统一异常捕获类
 * @author 1159588554@qq.com
 * @date 2020/5/22 10:38
 */
@ControllerAdvice
public class ExceptionCatch {
    /**
     * 定义日志
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    public static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    public static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static{
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }


    /**
     * 捕获自定义异常CustomException
     * @param customException 自定义异常
     * @return 相应结果
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        customException.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",customException.getMessage());
        //获得操作码
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }

    /**
     *
     * @param exception 异常类
     * @return 相应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        exception.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",exception.getMessage());
        if(EXCEPTIONS == null){
            //构建EXCEPTIONS
            EXCEPTIONS = builder.build();
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if(resultCode !=null){
            return new ResponseResult(resultCode);
        }else{
            //返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
    }
}
