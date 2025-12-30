package com.example.request.response;

import com.example.request.dto.AssignRequestDTO;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({ "status", "requests" })
public class RequestResponse {
    private String status;
    private List<? extends AssignRequestDTO> requests;

    public RequestResponse(List<? extends AssignRequestDTO> requests) {
        this.status = "success";
        this.requests = requests;
    }
}
