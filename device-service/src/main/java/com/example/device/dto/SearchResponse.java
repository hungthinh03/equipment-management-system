package com.example.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "results" })
public class SearchResponse {
    private String status;
    private SearchResultDTO result;
    private List<SearchResultDTO> results;

    public SearchResponse(List<SearchResultDTO> results) {
        this.status = "success";
        this.results = results;
    }

    public SearchResponse(SearchResultDTO result) {
        this.status = "success";
        this.result = result;
    }
}
