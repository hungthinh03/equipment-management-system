package com.example.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO {
    private String token;
    private String error;
    private Integer statusCode;

    public ApiResponseDTO(String token) {
        this.token = token;
    }

    public ApiResponseDTO(Integer statusCode) {
        this.error = "error";
        this.statusCode = statusCode;
    }
}


