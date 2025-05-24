package org.tomkidWorld.angelsPlan.global.error;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Map<String, String> errors;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.errors = null;
    }

    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
        this.errors = null;
    }
} 