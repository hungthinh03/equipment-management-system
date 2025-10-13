package com.example.device.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MISSING_FIELDS(1003, "Missing required fields", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1005, "Insufficient permissions", HttpStatus.FORBIDDEN),
    INACCESSIBLE_TYPE(1006, "You are not authorized to access this device type", HttpStatus.FORBIDDEN),
    NOT_FOUND(1007, "Device not found", HttpStatus.NOT_FOUND),
    INACCESSIBLE(1008, "You are not authorized to access this device", HttpStatus.FORBIDDEN),
    INVALID_UUID(1012, "Invalid UUID", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1013, "Invalid device status", HttpStatus.BAD_REQUEST),
    INVALID_DATE(1014, "Date must be in format yyyy-MM-dd", HttpStatus.BAD_REQUEST),
    DUPLICATE_SERIAL(1015, "Serial number already exists", HttpStatus.CONFLICT),
    TYPE_NOT_FOUND(1016, "Device type not found", HttpStatus.NOT_FOUND),
    INVALID_OPERATION(1017, "Device not in a valid state for this action", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}