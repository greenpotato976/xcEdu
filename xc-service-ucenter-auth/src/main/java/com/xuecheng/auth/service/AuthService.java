package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 13:15
 */
@Service
public class AuthService {
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    /**
     * 获得令牌
     * 调用springSecurity获得令牌
     * 将令牌信息存储到redis中
     * @param username 用户名
     * @param password 密码
     * @param clientId 客户端id
     * @param clientSecret 客户端密码
     * @return 令牌
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //调用springSecurity获得令牌
        AuthToken authToken = this.applyToken(username,password,clientId,clientSecret);
        if(authToken == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //将令牌信息存储到redis中
        //准备参数
        String access_token = authToken.getAccess_token();
        String content = JSON.toJSONString(authToken);
        boolean result = this.saveToken(access_token,content,tokenValiditySeconds);
        if(!result){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    /**
     * 将令牌储存在redis中
     * @param accessToken 身份令牌
     * @param content 令牌对象json数据
     * @param tokenValiditySeconds 失效时间
     * @return true代表成功
     */
    private boolean saveToken(String accessToken, String content, int tokenValiditySeconds) {
        String key = "user_token:" + accessToken;
        stringRedisTemplate.boundValueOps(key).set(content,tokenValiditySeconds, TimeUnit.SECONDS);
        //根据key查询是否储存成功
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 调用springSecurity获得令牌
     * @param username 用户名
     * @param password 密码
     * @param clientId 客户端id
     * @param clientSecret 客户端密码
     * @return 令牌对象
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //准备Url
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String url = uri + "/auth/oauth/token";
        //准备请求头
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization",httpBasic);
        //准备请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);
        //准备http节点
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //发起请求
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map tokenMap = responseEntity.getBody();
        if(tokenMap == null ||
                tokenMap.get("access_token") == null ||
                tokenMap.get("refresh_token") == null ||
                tokenMap.get("jti") == null){
            //解析错误信息
            if(tokenMap!= null || tokenMap.get("error_description")!=null){
                String error_description = (String) tokenMap.get("error_description");
                if(error_description.indexOf("UserDetailsService returned null")>=0){
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }else if(error_description.indexOf("坏的凭证")>=0){
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }
            return null;
        }
        AuthToken authToken = new AuthToken();
        //组装对象
        authToken.setAccess_token((String) tokenMap.get("jti"));
        authToken.setRefresh_token((String) tokenMap.get("refresh_token"));
        authToken.setJwt_token((String) tokenMap.get("access_token"));
        return authToken;
    }

    /**
     * 将客户端id 和密码进行bese64转码进行http base认证
     * @param clientId 客户端id
     * @param clientSecret 客户端密码
     * @return 转码后的串
     */
    private String getHttpBasic(String clientId,String clientSecret){
        String string = clientId+":"+clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


    /**
     * 根据身份令牌从redis中查询jwt令牌
     * @param uid cookie中的身份令牌
     * @return jwt令牌对象
     */
    public AuthToken getUserToken(String uid) {
        String userToken = "user_token:"+uid;
        String userTokenString = stringRedisTemplate.opsForValue().get(userToken);
        if(userTokenString!=null){
            //取出了令牌
            try {
                AuthToken authToken = JSON.parseObject(userTokenString, AuthToken.class);
                return authToken;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 根据身份令牌删除jwt令牌
     * @param uid 身份令牌
     * @return true表示成功
     */
    public boolean delToken(String uid) {
        String userToken = "user_token:"+uid;
        stringRedisTemplate.delete(userToken);
        return true;
    }
}
