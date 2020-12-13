package com.lin.common.dao.prdDao;

import com.lin.common.dto.PrdUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PrdUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PrdUser record);

    int insertSelective(PrdUser record);

    PrdUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PrdUser record);

    int updateByPrimaryKey(PrdUser record);
}