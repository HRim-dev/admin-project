package com.hrimDev.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrimDev.common.util.IpUtil;
import com.hrimDev.jwt.service.JwtService;
import com.hrimDev.jwt.util.CookieUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter { //요청에 한번만 생성되는 filter

    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    private final String[] permitAllPaths;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(permitAllPaths)
                .map(AntPathRequestMatcher::new)
                .anyMatch(matcher -> matcher.matches(request));
    }


    /**
     * 토큰 검증
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String domain = request.getServerName();

        try{
            //쿠키에서 JWT 토큰 추출
            String token = extractTokenFromCookie(request);
            if(StringUtils.hasText(token)) {
                //토큰이 있는 경우
                // 토큰 검중
                Authentication authToken = jwtService.getAuthentication(token);
                //세션에 사용자 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);

                //검증 통과
                //새로운 토큰 발급
                String newToken = jwtService.refreshAccessToken(token);
                ResponseCookie newCookie = cookieUtil.createTokenCookie("login_token", newToken, domain);
                response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());

                log.debug("JWT 토큰 갱신 완료");
                filterChain.doFilter(request, response);

            } else {
                log.warn("JWT token compact of handler are invalid. 토큰 인증 정보 불일치.");
                SecurityContextHolder.clearContext();
                //쿠키 삭제
                throw new JwtException("로그인 정보가 존재하지 않습니다.");
            }
        } catch (JwtException e) {
            log.warn("JWT 에러:{}", e.getMessage());
            log.error("error occured: ", e);
            SecurityContextHolder.clearContext();
            //쿠키 삭제
            ResponseCookie accessTokenCookie = cookieUtil.deleteTokenCookie("login_token", domain);
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=EUC-KR");
            response.setCharacterEncoding("EUC-KR");
            setErrorResponse(request, HttpServletResponse.SC_UNAUTHORIZED, response, e.getMessage());
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("login_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 에러 메시지 반환
     * @param status
     * @param response
     * @param message
     * @throws IOException
     */
    private void setErrorResponse(HttpServletRequest request, int status, HttpServletResponse response, String message) throws IOException {
        log.warn("ERROR MESSAGE:{}", message);
        response.setStatus(status);

        String clientIp = IpUtil.getClientIp(request);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");  // UTF-8 인코딩 설정

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status);
        responseBody.put("errorCode", "TOKEN_INVALID");
        responseBody.put("error", message);
        responseBody.put("timestamp", System.currentTimeMillis());

        // JSON 직렬화 및 응답 반환
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }

    // 클라이언트 IP를 가져오는 메서드
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            // X-Forwarded-For에 여러 IP가 있을 수 있어서 첫 번째 것만 사용
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}