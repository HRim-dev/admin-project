package com.hrimDev.login.dao;

import com.hrimDev.login.dto.LoginRequest;
import com.hrimDev.login.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

@Mapper
public interface LoginMapper {
    // 로그인호출 시 로그인한 사용자 정보 조회
    public UserDto getLoginUserInfo(@Param("userId") String userId) throws DataAccessException;

    //CustomUserDetailService를 위해
    public LoginRequest getLoginRequest(@Param("userId") String userId) throws DataAccessException;

    //로그인 실패 횟수 조회
    public Integer getLoginFailCount(@Param("userId") String userId) throws DataAccessException;

    // 로그인 실패시, loginFailCount 증가
    public void increaseLoginFailCount(@Param("userId") String userId) throws DataAccessException;

    public void resetLoginFailCount(@Param("userId") String userId) throws DataAccessException;
}
