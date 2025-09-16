package com.example.device.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(1003, "Missing required fields"),
    UNAUTHORIZED(1005, "Insufficient permissions");

    private final int code;
    private final String message;
}

