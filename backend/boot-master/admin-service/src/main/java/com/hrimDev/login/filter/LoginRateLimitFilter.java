package com.hrimDev.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrimDev.login.dto.UserDto;
import com.hrimDev.login.service.LoginService;
import com.hrimDev.login.serviceImpl.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_URI = "/login";
    private static final String METHOD_POST = "POST";
    private static final int MAX_FAIL_COUNT = 5;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LoginService loginService;

    public LoginRateLimitFilter(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        boolean isLoginRequest = "/login".equals(request.getRequestURI()) &&
                "POST".equalsIgnoreCase(request.getMethod());

        if (isLoginRequest) {
            try {
                HttpServletRequest wrappedRequest = request instanceof CachedBodyHttpServletRequest
                        ? request
                        : new CachedBodyHttpServletRequest(request);

                UserDto loginRequest = objectMapper.readValue(wrappedRequest.getInputStream(), UserDto.class);
                String userId = loginRequest.getUserId();

                if (loginService.getLoginFailCount(userId) >= MAX_FAIL_COUNT) {
                    sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "로그인 시도 가능 횟수를 초과했습니다.\n 아이디 찾기/비밀번호 찾기해주세요.");
                    return;
                }

                filterChain.doFilter(wrappedRequest, response);
                return;

            } catch (IOException e) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 요청 형식입니다.");
                return;
            } catch (Exception e) {
                log.error("LoginRateLimitFilter error: {}", e.getMessage(), e);

                if (e instanceof ResponseStatusException ex) {
                    sendJsonError(response, ex.getStatusCode().value(), ex.getReason());
                } else {
                    sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 필터 처리 중 오류가 발생했습니다.");
                }
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", statusCode);
        errorResponse.put("error", message); // \n 포함되어도 자동 이스케이프 처리

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

}
