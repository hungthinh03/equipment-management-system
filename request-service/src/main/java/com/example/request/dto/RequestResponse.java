package com.example.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResponse {
    private String status;
    private ViewRequestDTO request;

    public RequestResponse(ViewRequestDTO request) {
        this.status = "success";
        this.request = request;
    }
}
