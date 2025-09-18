package com.example.device.common.exception;

import com.example.device.dto.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Mono<ResponseEntity<ApiResponseDTO>> handleAppException(AppException ex) {
        return Mono.just(
                ResponseEntity
                        .status(ex.getErrorCode().getHttpStatus())
                        .body(new ApiResponseDTO(ex.getErrorCode()))
        );
    }
}