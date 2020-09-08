package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/23 19:30
 */
@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 从 cookie 查询用户身份令牌是否存在，不存在则拒绝访问
     * @param request 请求对象
     * @return cookie中的串
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> uid = CookieUtil.readCookie(request, "uid");
        String accessToken  = uid.get("uid");
        if(StringUtils.isEmpty(accessToken)){
            return null;
        }
        return accessToken;
    }

    /**
     * 从 http header 查询jwt令牌是否存在，不存在则拒绝访问
     * @param request 请求对象
     * @return 头信息
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            return null;
        }
        if(!authorization.startsWith("Bearer ")){
            return null;
        }
        return authorization;
    }

    /**
     * 从 Redis 查询 user_token 令牌是否过期，过期则拒绝访问
     * @param tokenFromCookie 身份令牌
     * @return jwt令牌存在时间
     */
    public long getExpire(String tokenFromCookie) {
        String key = "user_token:" + tokenFromCookie;
        Long expire = stringRedisTemplate.getExpire(key);
        return expire;
    }
}
