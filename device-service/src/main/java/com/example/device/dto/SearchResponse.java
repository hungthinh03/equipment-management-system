package com.example.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "status", "results" })
public class SearchResponse {
    private String status;
    private Integer page;
    private Integer size;
    private Integer totalItems;
    private List<SearchResultDTO> results;

    public SearchResponse(List<SearchResultDTO> results, Integer page, Integer size, Integer totalItems) {
        this.status = "success";
        this.results = results;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
    }

    public SearchResponse(List<SearchResultDTO> results) {
        this.status = "success";
        this.results = results;
    }

}
