package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 17:07
 */
@Service
public class UserService {
    @Autowired
    XcUserRepository xcUserRepository;
    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;
    @Autowired
    XcMenuMapper xcMenuMapper;

    /**
     * 获得用户扩展对象
     * @param username 用户名
     * @return 用户扩展对象
     */
    public XcUserExt getUserExt(String username) {
        //参数校验
        if(username == null || StringUtils.isEmpty(username)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //根据用户名查询用户对象
        XcUser xcUser = this.getXcUserByUsername(username);
        if(xcUser == null){
            return null;
        }
        //获得userId
        String userId = xcUser.getId();
        //根据UserId查询公司与用户关联对象
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        //学生没有公司与用户关联对象
        String companyId = null;
        if(xcCompanyUser != null ){
            companyId = xcCompanyUser.getCompanyId();
        }
        //根据UserId查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        //准备返回值
        XcUserExt xcUserExt = new XcUserExt();
        //组装对象
        BeanUtils.copyProperties(xcUser,xcUserExt);
        xcUserExt.setCompanyId(companyId);
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }

    private XcUser getXcUserByUsername(String username){
        return xcUserRepository.findByUsername(username);
    }
}
