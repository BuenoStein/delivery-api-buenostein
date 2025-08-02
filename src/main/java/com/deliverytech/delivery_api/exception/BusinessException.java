package com.deliverytech.delivery_api.exception;

public class BusinessException extends RuntimeException {
    
    private final String code;

    public BusinessException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
