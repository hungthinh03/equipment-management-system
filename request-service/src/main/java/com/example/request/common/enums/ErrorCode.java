package com.example.request.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(1005, "Insufficient permissions", HttpStatus.FORBIDDEN),
    DEVICE_NOT_FOUND(1007, "Device not found", HttpStatus.NOT_FOUND),
    NOT_FOUND(1009, "Request not found", HttpStatus.NOT_FOUND),
    INVALID_OPERATION(1010, "Request not in a valid state for this action", HttpStatus.FORBIDDEN),
    DEVICE_UNAVAILABLE(1011, "Device is currently in use or unavailable", HttpStatus.CONFLICT),
    INVALID_UUID(1012, "Invalid UUID", HttpStatus.BAD_REQUEST),
    ALREADY_REQUESTED_CLOSE(1013, "Close request already submitted", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}