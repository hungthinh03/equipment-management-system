package com.example.gateway.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_TOKEN(1004, "Token is invalid or expired");

    private final int code;
    private final String message;
}

