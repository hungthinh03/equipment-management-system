package com.example.request.common.exception;

import com.example.request.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Mono<ResponseEntity<ApiResponse>> handleAppException(AppException ex) {
        return Mono.just(
                ResponseEntity
                        .status(ex.getErrorCode().getHttpStatus())
                        .body(new ApiResponse(ex.getErrorCode()))
        );
    }
}