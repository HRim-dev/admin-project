package com.hrimDev.signup.service;

import com.hrimDev.signup.dto.SignUpDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface SignUpService {
    public boolean checkDuplicateByUserId(String userId) throws Exception;
    public int signUpUserInfo(SignUpDto signUpDto) throws Exception;
}
