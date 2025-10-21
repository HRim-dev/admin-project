package org.hrimDev.signup.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

@Mapper
public interface SignUpMapper {
    public int countByUserId(@Param("userId") String userId) throws DataAccessException;
}
