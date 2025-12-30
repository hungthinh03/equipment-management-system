package com.example.request.response;

import com.example.request.dto.ViewMyRequestDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyRequestResponse {
    private String status;
    private List<ViewMyRequestDTO> requests;

    public MyRequestResponse(List<ViewMyRequestDTO> request) {
        this.status = "success";
        this.requests = request;
    }
}
