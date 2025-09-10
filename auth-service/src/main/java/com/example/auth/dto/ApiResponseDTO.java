package com.example.auth.dto;

import com.example.auth.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO {
    private String status;
    private String token;
    private Integer statusCode;
    private String message;

    public ApiResponseDTO(String token) {
        this.status = "success";
        this.token = token;
    }

    public ApiResponseDTO(ErrorCode errorCode) {
        this.status = "error";
        this.statusCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}


