package com.example.device.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(1003, "Missing required fields", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1005, "Insufficient permissions", HttpStatus.FORBIDDEN),
    INVALID_TYPE(1006, "Device type not found or inaccessible", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1007, "Device not found", HttpStatus.NOT_FOUND),
    INACCESSIBLE(1008, "Device inaccessible", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}