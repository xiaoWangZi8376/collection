package com.lin.service.impl;

import com.lin.common.dao.prdDao.PrdUserMapper;
import com.lin.common.dto.PrdUser;
import com.lin.service.service.PrdUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrdUserServiceImpl implements PrdUserService {
    @Autowired
    private PrdUserMapper prdUserMapper;

    @Override
    public int deleteByPrimaryKey(String id) {
        return prdUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(transactionManager = "prdTransactionManager", rollbackFor = Exception.class)
    public int insert(PrdUser record) {
        int insert = prdUserMapper.insert(record);
//        int i = 1 / 0;
        return insert;
    }

    @Override
    public int insertSelective(PrdUser record) {
        return prdUserMapper.insertSelective(record);
    }

    @Override
    public PrdUser selectByPrimaryKey(String id) {
        return prdUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PrdUser record) {
        return prdUserMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PrdUser record) {
        return prdUserMapper.updateByPrimaryKey(record);
    }
}
