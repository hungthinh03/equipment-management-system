package com.example.device.response;

import com.example.device.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private String status;
    private Integer id;
    private UUID uuid;
    private Integer statusCode;
    private String message;

    public ApiResponse(Integer id) {
        this.status = "success";
        this.id = id;
    }

    public ApiResponse(Integer id, UUID uuid) {
        this.status = "success";
        this.id = id;
        this.uuid = uuid;
    }

    public ApiResponse(String message) {
        this.status = "success";
        this.message = message;
    }

    public ApiResponse(ErrorCode errorCode) {
        this.status = "error";
        this.statusCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }


}


