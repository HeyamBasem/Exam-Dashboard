package com.examdash.assessment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for performing a full update of an assessment")
public class UpdateAssessmentRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    @Schema(description = "The title of the assessment (required for full update)", example = "Advanced Mathematics Final", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
    private String title;

    @Schema(description = "Optional description of the assessment", example = "Updated final assessment")
    private String description;

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    @Schema(description = "The subject area (required for full update)", example = "Math", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String subject;

    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    @Schema(description = "The target grade level (required for full update)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer grade;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    @Schema(description = "The total marks achievable (required for full update)", example = "120", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer totalMarks;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 480 minutes")
    @Schema(description = "The allowed duration in minutes (required for full update)", example = "90", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "480")
    private Integer durationMinutes;
}
