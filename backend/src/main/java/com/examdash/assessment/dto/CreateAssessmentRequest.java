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
@Schema(description = "Payload for creating a new assessment")
public class CreateAssessmentRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    @Schema(description = "The title of the assessment", example = "Mathematics Final Exam", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 255)
    private String title;

    @Schema(description = "Optional description of the assessment", example = "Final mathematics assessment for grade 10")
    private String description;

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    @Schema(description = "The subject area", example = "Math", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String subject;

    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    @Schema(description = "The target grade level", example = "10", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer grade;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    @Schema(description = "The total marks achievable", example = "100", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer totalMarks;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 480 minutes")
    @Schema(description = "The allowed duration in minutes", example = "60", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "480")
    private Integer durationMinutes;
}
