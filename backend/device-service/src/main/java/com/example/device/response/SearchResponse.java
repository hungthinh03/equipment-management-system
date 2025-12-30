package com.example.device.response;

import com.example.device.dto.SearchResultDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "status", "results" })
public class SearchResponse {
    private String status;
    private Integer page;
    private Integer size;
    private Integer totalItems;
    private List<SearchResultDTO> results;
    private SearchResultDTO result; // exact response USED by request-service

    public SearchResponse(List<SearchResultDTO> results, Integer page, Integer size, Integer totalItems) {
        this.status = "success";
        this.results = results;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
    }

    public SearchResponse(SearchResultDTO result) {
        this.status = "success";
        this.result = result;
    }

}
