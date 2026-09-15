package com.examdash.assessment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to confirm and save a previewed imported assessment")
public class ConfirmImportRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    @Schema(description = "Assessment title", example = "Math Quiz Chapter 5")
    private String title;

    @Schema(description = "Optional description", example = "Weekly math quiz")
    private String description;

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    @Schema(description = "Subject area", example = "Math")
    private String subject;

    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    @Schema(description = "Grade level", example = "10")
    private Integer grade;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    @Schema(description = "Total marks", example = "50")
    private Integer totalMarks;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 480 minutes")
    @Schema(description = "Duration in minutes", example = "30")
    private Integer durationMinutes;

    @NotEmpty(message = "Questions list cannot be empty")
    @Valid
    @Schema(description = "List of questions to save")
    private List<QuestionRequest> questions;
}