package com.hrimDev.signup.dao;

import com.hrimDev.signup.dto.SignUpDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

@Mapper
public interface SignUpMapper {
    //아이디 중복확인
    public int countByUserId(@Param("userId") String userId) throws DataAccessException;
    //회원가입
    public int signUpUserInfo(SignUpDto signUpDto) throws DataAccessException;
}
