package com.hrimDev.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hrimDev.common.exception.DuplicateResourceException;
import com.hrimDev.common.exception.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class RestControllerExceptionAdvice {
    @ExceptionHandler(value={Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGlobalException(Exception e, HttpServletRequest req, HttpServletResponse res) {
        log.error("error occured: ", e);
        Throwable rootCause = e.getCause();
        if (rootCause instanceof InvalidFormatException) {
            log.error("InvalidFormatException 발생[{}]", e.getMessage());
            return ErrorResponseBuilder.makeResponseEntity(HttpStatus.BAD_REQUEST, "INVALID_FORMAT", "허용하지 않는 문자열이 있습니다.", req.getRequestURI());
        }

        log.error("GlobalException 발생[{}]", e.getMessage());
        String eMessage = "오류가 발생 하였습니다.\n 관리자에게 문의 하세요.";
        return ErrorResponseBuilder.makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_EXCEPTION", eMessage, req.getRequestURI());
    }


    //409 Conflict 에러
    @ExceptionHandler(value = {ResponseStatusException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Object handleResponseStatusException(ResponseStatusException e, HttpServletRequest req, HttpServletResponse res) {
        log.error("ResponseStatusException [{}]", e.getMessage());
        return ErrorResponseBuilder.makeResponseEntity(HttpStatus.CONFLICT, "CONFLICT_ERROR", e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(value={DuplicateResourceException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    Object handleDuplicateResourceException(DuplicateResourceException e , HttpServletRequest req, HttpServletResponse res) {
        String eMessage = e.getMessage();
        log.error("DuplicateResourceException: {}", eMessage);
        log.error("error occured: ", e);

        return ErrorResponseBuilder.makeResponseEntity(HttpStatus.CONFLICT, "DUPLICATE_ERROR", eMessage, req.getRequestURI());

    }

    //500 에러
    @ExceptionHandler(value = {HttpServerErrorException.InternalServerError.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleInternalException(HttpServerErrorException.InternalServerError e, HttpServletRequest req, HttpServletResponse res) {
        log.error("InternalServerError [{}]", e.getCause().getMessage());
        log.error("error occured: ", e);
        String eMessage = "오류가 발생 하였습니다.\n 관리자에게 문의 하세요.";
        return ErrorResponseBuilder.makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", eMessage, req.getRequestURI());

    }

}
