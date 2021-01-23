package com.lin.service.impl;


import com.lin.common.dto.SysUser;
import com.lin.common.entity.User;
import com.lin.common.stgDao.SysUserMapper;
import com.lin.service.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getUserById(Long id) {
        return sysUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public void  insert(SysUser sysUser) {
        int insert = sysUserMapper.insert(sysUser);
        System.out.println(insert);
    }

    @Override
    public List<User> listUsers() {
        return null;
    }

    @Override
    public void deleteUserById(Long id) {
        sysUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateUser(SysUser sysUser) {
        sysUserMapper.updateByPrimaryKey(sysUser);
    }

}
