package com.hrimDev.jwt.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class CookieUtil {

    private final static String SAME_SITE = "Strict"; //""Lax";

    /**
     * 쿠키 생성
     * @param name
     * @param value
     * @param domain
     * @return
     */
    public ResponseCookie createTokenCookie(String name, String value, String domain) {
        if(domain == null) {
            domain = "localhost";
        }
        log.info("쿠키 생성 완료");
        return ResponseCookie.from(name, value)
                .httpOnly(true)
//                    .domain(domain) //쿠키가 유효한 도메인
                .path("/")  //쿠키가 유효한 경로
                .maxAge(Duration.ofDays(1)) //쿠키 만료시간(초단위)
                .sameSite(SAME_SITE)
                .build();
    }

    /**
     * 쿠키 제거
     * @param name
     * @param domain
     * @return
     */
    public ResponseCookie deleteTokenCookie(String name, String domain) {
        if(domain == null) {
            domain = "localhost";
        }
        return ResponseCookie.from(name, "")
                .httpOnly(true)
//                .domain(domain) //쿠키가 유효한 도메인
                .path("/")  //쿠키가 유효한 경로
                .maxAge(0) //쿠키 만료시간: 0은 삭제
                .sameSite(SAME_SITE)
                .build();
    }

}
