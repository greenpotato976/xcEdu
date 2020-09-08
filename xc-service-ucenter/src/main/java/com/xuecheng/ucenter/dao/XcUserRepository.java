package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 17:08
 */
public interface XcUserRepository extends JpaRepository<XcUser,String> {
    XcUser findByUsername(String username);
}
