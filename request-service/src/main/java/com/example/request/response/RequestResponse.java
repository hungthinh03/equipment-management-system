package com.example.request.response;

import com.example.request.dto.RequestDTO;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({ "status", "requests" })
public class RequestResponse {
    private String status;
    private List<RequestDTO> requests;

    public RequestResponse(List<RequestDTO> requests) {
        this.status = "success";
        this.requests = requests;
    }
}
