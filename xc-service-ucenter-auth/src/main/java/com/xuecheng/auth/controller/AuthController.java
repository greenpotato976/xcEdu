package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 13:05
 */
@RestController
public class AuthController implements AuthControllerApi {
    @Autowired
    AuthService authService;

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;

    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;


    /**
     * 登录请求令牌
     * 获得令牌
     * 存储cookie
     * @param loginRequest 用户名密码和验证码
     * @return 用户身份令牌
     */
    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        //参数校验
        if(loginRequest == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String username = loginRequest.getUsername();
        if(username == null || StringUtils.isEmpty(username)){
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        String password = loginRequest.getPassword();
        if(password == null || StringUtils.isEmpty(password)){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        //获得令牌
        AuthToken authToken = authService.login(username,password,clientId,clientSecret);
        //获得用户身份令牌
        String accessToken = authToken.getAccess_token();
        //将身份令牌存储到cookie中
        this.saveCookie(accessToken);

        return new LoginResult(CommonCode.SUCCESS,accessToken);
    }

    /**
     * 将身份令牌存储到cookie中
     * @param accessToken 身份令牌
     */
    private void saveCookie(String accessToken) {
        //httpResponse
        HttpServletResponse httpResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //存储cookie
        CookieUtil.addCookie(httpResponse,cookieDomain,"/","uid",accessToken,cookieMaxAge,false);
    }

    /**
     * 用户退出
     * 删除cookie
     * 删除redis中的令牌
     * @return
     */
    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        //取出cookie中的用户身份令牌
        String uid = this.getTokenFormCookie();
        //删除redis中的令牌
        boolean result = authService.delToken(uid);
        //清除cookie
        this.clearCookie(uid);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 清除cookie
     * @param uid 身份令牌
     */
    private void clearCookie(String uid) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,cookieDomain,"/","uid",uid,0,false);

    }

    /**
     * 获得jwt令牌
     * 取出cookie中的身份令牌
     * 查询redis中的jwt令牌
     * @return jwt令牌
     */
    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {
        //取出cookie中的身份令牌
        String uid = getTokenFormCookie();
        if(uid == null || StringUtils.isEmpty(uid)){
            return new JwtResult(CommonCode.FAIL,null);
        }
        //查询redis中的jwt令牌
        AuthToken authToken = authService.getUserToken(uid);
        if(authToken != null){
            //将jwt令牌返回给用户
            String jwtToken = authToken.getJwt_token();
            return new JwtResult(CommonCode.SUCCESS,jwtToken);
        }
        return null;
    }

    /**
     * 从cookie中获得身份令牌
     * @return 身份令牌
     */
    private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if(map!=null && map.get("uid")!=null){
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }
}
