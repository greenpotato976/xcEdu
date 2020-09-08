package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/24 16:55
 */
@Mapper
public interface XcMenuMapper {
    /**
     * 根据用户id查询所属权限
     * @param userId 用户id
     * @return 权限集合
     */
    List<XcMenu> selectPermissionByUserId(String userId);
}
