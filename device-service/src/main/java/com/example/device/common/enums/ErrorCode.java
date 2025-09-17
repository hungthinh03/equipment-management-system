package com.example.device.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(1003, "Missing required fields"),
    INVALID_TYPE(1005, "Device type not found or inaccessible");

    private final int code;
    private final String message;
}

