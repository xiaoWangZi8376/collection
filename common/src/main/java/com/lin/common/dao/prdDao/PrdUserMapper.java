package com.lin.common.dao.prdDao;

import com.lin.common.dto.PrdUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PrdUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(PrdUser record);

    int insertSelective(PrdUser record);

    PrdUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PrdUser record);

    int updateByPrimaryKey(PrdUser record);
}