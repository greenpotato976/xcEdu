package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.ToString;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/20 20:21
 */
@ToString
public enum  LearningCode implements ResultCode {
    LEARNING_GET_MEDIA_ERROR(false,23001,"学习中心获取媒资信息错误！"),
    CHOOSECOURSE_USERISNULl(false,23002,"选课程序用户id为空"),
    CHOOSECOURSE_TASKISNULL(false,23003,"选课任务对象为空");

    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
