package com.hrimDev.login.service;

import com.hrimDev.login.dto.LoginRequest;
import com.hrimDev.login.dto.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginService {

    LoginResponse login(LoginRequest loginRequest);

    /* 로그인 시도 관리*/
    /* block 된 사용자인지 판단*/
    int getLoginFailCount(String managerId) throws Exception;

    /* 로그인 실패 횟수 증가*/
    void increaseLoginFailCount(String managerId) throws Exception;

    /* 로그인 실패 횟수 초기화*/
    void resetLoginFailCount(String managerId) throws Exception;

    /*탈퇴한 사용자인지 확인*/
    String checkDeleteUser(HttpServletResponse response, String managerId) throws Exception;

}
