package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/22 17:08
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {
    /**
     * 根据用户id查询所属企业id
     * @param userId 用户id
     * @return 公司和用户关联对象
     */
    XcCompanyUser findByUserId(String userId);
}
