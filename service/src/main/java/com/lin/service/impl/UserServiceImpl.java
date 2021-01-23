package com.lin.service.impl;

import com.lin.common.dto.User;
import com.lin.common.devDao.UserMapper;
import com.lin.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    /**
     * 问题一：用springboot注入mapper的时候出现下波浪线的错误，
     * 并且提示“Could not autowire. No beans of ‘UserMapper’ type found”
     *
     * 第一步：在application中加入MapperScan的注解，里面写mapper的目录路径
     * 第二步：在mapper中加入@Repository注解和@Mapper注解
     */
    @Autowired
    private UserMapper userMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(User record) {
        return userMapper.insert(record);
    }

    @Override
    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    @Override
    public User selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }
}
