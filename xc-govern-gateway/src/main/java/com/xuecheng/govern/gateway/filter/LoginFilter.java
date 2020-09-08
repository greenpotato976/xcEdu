package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/23 16:56
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;
    /**
     * pre：请求在被路由之前执行
     * routing：在路由请求时调用
     * post：在routing和errror过滤器之后调用
     * error：处理请求时发生错误调用
     * @return 过滤器类型
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过虑器序号，越小越被优先执行
     * @return 过滤器优先级
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 过滤器启动
     * @return true表示启动
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过虑器的内容
     * @return 测试的需求：过虑所有请求，判断头部信息是否有Authorization，如果没有则拒绝访问，否则转发到微服务。
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //拿到request对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //获取response对象
        HttpServletResponse response = requestContext.getResponse();
        //从 cookie 查询用户身份令牌是否存在，不存在则拒绝访问
        String tokenFromCookie = authService.getTokenFromCookie(request);
        if(StringUtils.isEmpty(tokenFromCookie)){
            //拒绝访问
            this.accessDenied();
            return null;
        }
        //从 http header 查询jwt令牌是否存在，不存在则拒绝访问
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if(StringUtils.isEmpty(jwtFromHeader)){
            //拒绝访问
            this.accessDenied();
            return null;
        }
        //从 Redis 查询 user_token 令牌是否过期，过期则拒绝访问
        long expire = authService.getExpire(tokenFromCookie);
        if(expire < 0 ){
            //拒绝访问
            this.accessDenied();
            return null;
        }
        return null;
    }

    /**
     * 拒绝访问
     */
    private void accessDenied(){
        //拿到request对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //获取response对象
        HttpServletResponse response = requestContext.getResponse();
        //拒绝访问
        //设置响应代码
        requestContext.setResponseStatusCode(200);
        //构造相应信息
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String context = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(context);
        //设置响应头信息
        response.setContentType("application/json;charset=utf-8");
    }
}
