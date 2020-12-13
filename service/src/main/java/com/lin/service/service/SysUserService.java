package com.lin.service.service;


import com.lin.common.dto.SysUser;
import com.lin.common.entity.User;

import java.util.List;


public interface SysUserService {


    /**
     * 根据用户ID获得用户信息
     * @param id
     * @return
     */
    SysUser getUserById(Long id);

    /**
     * 根据用户名获得用户信息
     * @param sysUser
     * @return
     */
    void insert(SysUser sysUser);

    /**
     * 获得所有的用户
     * @return
     */
    List<User> listUsers();

    /**
     * 根据用户ID删除用户
     * @param id
     */
    void deleteUserById(Long id);

    /**
     * 更新用户
     * @param sysUser
     */
    void updateUser(SysUser sysUser);



}
