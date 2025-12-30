package com.example.gateway.dto;

import com.example.gateway.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO {
    private String status;
    private Integer statusCode;
    private String message;

    public ApiResponseDTO(ErrorCode errorCode) {
        this.status = "error";
        this.statusCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}


