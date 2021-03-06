package com.styx.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务异常
 *
 * @author TontZhou
 */
public class BusinessException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private Object data;
    private boolean hasChildException;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        hasChildException = true;
    }

    public BusinessException(String message, Object data, Throwable cause) {
        super(message, cause);
        this.data = data;
        hasChildException = true;
    }

    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BusinessException(HttpStatus httpStatus, String message, Object data) {
        super(message);
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public BusinessException(HttpStatus httpStatus, String message, Object data, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.data = data;
        hasChildException = true;
    }

    public Object getData() {
        return data;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public boolean isHasChildException() {
        return hasChildException;
    }

}
