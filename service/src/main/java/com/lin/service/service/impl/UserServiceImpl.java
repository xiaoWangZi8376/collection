package com.lin.service.service.impl;


import com.lin.common.dto.SysUser;
import com.lin.common.entity.User;
import com.lin.common.mapper.UserMapper;
import com.lin.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public SysUser getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public List<User> listUsers() {
        return null;
    }

    @Override
    public void deleteUserById(Integer id) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void insertUser(User user) {

    }
}
