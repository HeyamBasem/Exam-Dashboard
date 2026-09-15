package com.examdash.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic wrapper for paginated API responses")
public class PagedResponse<T> {

    @Schema(description = "The list of items for the current page")
    private List<T> content;
    
    @Schema(description = "The current page number (0-indexed)", example = "0")
    private int page;
    
    @Schema(description = "The size of the page (number of items requested)", example = "10")
    private int size;
    
    @Schema(description = "The total number of elements across all pages", example = "42")
    private long totalElements;
    
    @Schema(description = "The total number of pages available", example = "5")
    private int totalPages;
}
