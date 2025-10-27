package com.hrimDev.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrimDev.jwt.util.CookieUtil;
import com.hrimDev.login.dto.LoginRequest;
import com.hrimDev.login.dto.LoginResponse;
import com.hrimDev.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final CookieUtil cookieUtil;
    /* 로그인 */
    @PostMapping(value = "/login")
    public void login(@RequestBody LoginRequest loginRequest,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

        LoginResponse result = loginService.login(loginRequest);

        if (result == null) {
            // 에러 응답은 서비스 내부에서 이미 처리됨 (sendJsonError 사용)
            return;
        }

        // 로그인 성공 시 쿠키 설정
        String domain = request.getServerName();
        String accessToken = result.getAccessToken();
        ResponseCookie accessTokenCookie = cookieUtil.createTokenCookie("login_token", accessToken, domain);
        response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // JSON 응답 반환
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = new ObjectMapper().writeValueAsString(result.getUserDto());
        response.getWriter().write(json);
        response.getWriter().flush();

        log.info("SUCCESS ADMIN LOGIN");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutAdminManager(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("로그아웃 요청 수신");

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 쿠키 삭제
        String domain = request.getServerName(); // 도메인 추출
        ResponseCookie accessTokenCookie = cookieUtil.deleteTokenCookie("login_token", domain);
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // 로그아웃 성공 로그
        log.info("로그아웃 완료 - 쿠키 삭제 및 세션 종료");

        if(loginRequest.getUserId() != null) {
            try {
                loginService.resetLoginFailCount(loginRequest.getUserId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        // 필요 시 로그아웃 성공 후 리디렉션 (프론트에서 처리 권장)
        return ResponseEntity.ok().body(Map.of("scss_f", "Y"));
    }

}
