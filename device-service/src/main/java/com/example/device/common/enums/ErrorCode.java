package com.example.device.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(1001, "Missing required fields");

    private final int code;
    private final String message;
}

