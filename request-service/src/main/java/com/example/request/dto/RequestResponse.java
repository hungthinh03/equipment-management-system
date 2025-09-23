package com.example.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResponse {
    private String status;
    private List<ViewRequestDTO> requests;

    public RequestResponse(List<ViewRequestDTO> request) {
        this.status = "success";
        this.requests = request;
    }
}
