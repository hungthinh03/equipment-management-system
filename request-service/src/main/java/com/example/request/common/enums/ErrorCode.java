package com.example.request.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //INVALID_INPUT(1003, "Missing required fields", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1005, "Insufficient permissions", HttpStatus.FORBIDDEN),
    NOT_FOUND(1009, "Request not found", HttpStatus.NOT_FOUND),
    INACCESSIBLE(1010, "Request inaccessible", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}