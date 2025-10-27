package com.hrimDev.common.exception;

/***
 * 중복 값에 대한 예외
 */
public class DuplicateResourceException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException() {
        super();
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
