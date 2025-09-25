package com.example.request.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({ "status", "requests" })
public class PendingResponse {
    private String status;
    private List<PendingRequestDTO> requests;

    public PendingResponse(List<PendingRequestDTO> requests) {
        this.status = "success";
        this.requests = requests;
    }
}
