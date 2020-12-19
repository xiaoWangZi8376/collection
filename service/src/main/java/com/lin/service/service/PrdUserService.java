package com.lin.service.service;

import com.lin.common.dto.PrdUser;

public interface PrdUserService {

    int deleteByPrimaryKey(String id);

    int insert(PrdUser record);

    int insertSelective(PrdUser record);

    PrdUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PrdUser record);

    int updateByPrimaryKey(PrdUser record);
}
