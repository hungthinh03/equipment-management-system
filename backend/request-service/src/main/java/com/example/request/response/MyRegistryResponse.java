package com.example.request.response;

import com.example.request.dto.ViewMyRegistryDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "registries" })
public class MyRegistryResponse {
    private String status;
    private List<ViewMyRegistryDTO> registries;

    public MyRegistryResponse(List<ViewMyRegistryDTO> registries) {
        this.status = "success";
        this.registries = registries;
    }
}
