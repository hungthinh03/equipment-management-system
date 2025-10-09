package com.example.device.response;

import com.example.device.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private String status;
    private Integer id;
    private Integer statusCode;
    private String message;

    public ApiResponse(Integer id) {
        this.status = "success";
        this.id = id;
    }

    public ApiResponse(ErrorCode errorCode) {
        this.status = "error";
        this.statusCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }


}


