package com.hrimDev.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 에러 응답 ResponseEntity 생성
     *
     * 주로 컨트롤러(@RestController, @ControllerAdvice) 또는 핸들러(@ExceptionHandler) 등
     * Spring MVC 내부 흐름에서 예외 또는 에러를 ResponseEntity 형태로 반환할 때 사용됩니다.
     *
     * @param errorStatus HTTP 상태 코드 (숫자) 예: 400, 401, 500
     * @param errorCode   시스템 내부 에러 코드 (예: "E001", "AUTH_FAIL")
     * @param resultMsg   사용자에게 보여줄 에러 메시지
     * @param errorPath   에러가 발생한 요청 URI
     * @return ResponseEntity<Object> JSON 형식의 에러 정보를 담은 ResponseEntity
     */
    public static ResponseEntity<Object> makeResponseEntity(
            HttpStatus errorStatus,
            String errorCode,
            String resultMsg,
            String errorPath
    ) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("errorTime", LocalDateTime.now().format(dateFormat));
        errorMap.put("errorStatus", errorStatus.value());
        errorMap.put("errorCode", errorCode);
        errorMap.put("errorMessage", resultMsg);
        errorMap.put("errorPath", errorPath);

        return ResponseEntity
                .status(errorStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }

    /***
     * HttpServletResponse를 직접 조작하여 에러 응답을 JSON 형태로 작성
     *
     * Spring Security 필터, 서블릿 필터, 또는 스프링 컨트롤러 외부에서 발생하는 예외 처리에 사용됩니다.
     * HttpServletResponse 객체에 직접 JSON 바디를 작성하기 때문에 ResponseEntity를 사용할 수 없는 곳에서 활용됩니다.
     *
     * @param response  HttpServletResponse 객체
     * @param errorStatus
     * @param errorCode
     * @param resultMsg
     * @param errorPath
     */
    public static void writeErrorResponse(
            HttpServletResponse response,
            HttpStatus errorStatus,
            String errorCode,
            String resultMsg,
            String errorPath
    ) throws IOException {
        response.setStatus(errorStatus.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("errorTime", LocalDateTime.now().format(dateFormat));
        errorMap.put("errorStatus", errorStatus);
        errorMap.put("errorCode", errorCode);
        errorMap.put("errorMessage", resultMsg);
        errorMap.put("errorPath", errorPath);

        new ObjectMapper().writeValue(response.getWriter(), errorMap);
    }
}
