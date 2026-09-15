package com.examdash.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Indicates whether the API request was successful", example = "true")
    private boolean success;
    
    @Schema(description = "A human-readable message about the result", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "The actual response payload (if any)")
    private T data;
    
    @Schema(description = "List of validation field errors, if any")
    private List<FieldError> errors;
    
    // Fixed: Using Instant enforces UTC to prevent timezone ambiguity
    @Schema(description = "The timestamp of the response in UTC", example = "2026-08-09T15:00:00Z")
    private Instant timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, List<FieldError> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(Instant.now())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Represents a single validation error on a field")
    public static class FieldError {
        @Schema(description = "The name of the field that failed validation", example = "title")
        private String field;
        
        @Schema(description = "The validation error message", example = "Title is required")
        private String message;
    }
}