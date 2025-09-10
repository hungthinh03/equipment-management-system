package com.example.auth.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(1001, "Email and password are required"),
    INVALID_INFO(1002, "Email or password is incorrect");


    private final int code;
    private final String message;
}

