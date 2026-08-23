package com.examdash.assessment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant; // we changed to Instant for consistency as we changed it in the assessment entity as well, so that the timestamps are consistent across the application

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing assessment details")
public class AssessmentResponse {

    @Schema(description = "The unique identifier of the assessment", example = "1")
    private Long id;
    
    @Schema(description = "The title of the assessment", example = "Mathematics Final Exam")
    private String title;
    
    @Schema(description = "The description of the assessment", example = "Final mathematics assessment for grade 10")
    private String description;
    
    @Schema(description = "The subject area", example = "Math")
    private String subject;
    
    @Schema(description = "The target grade level", example = "10")
    private Integer grade;
    
    @Schema(description = "The total marks achievable", example = "100")
    private Integer totalMarks;
    
    @Schema(description = "The allowed duration in minutes", example = "60")
    private Integer durationMinutes;
    
    @Schema(description = "The email of the user who created the assessment", example = "teacher@example.com")
    private String createdBy;
    
    @Schema(description = "The timestamp when the assessment was created", example = "2026-08-09T15:00:00Z")
    private Instant createdAt; // Changed it from LocalDateTime to Instant for consistency with the entity and to ensure that timestamps are stored in UTC format across the application.
    
    @Schema(description = "The timestamp when the assessment was last updated", example = "2026-08-09T15:30:00Z")
    private Instant updatedAt; // Changed it from LocalDateTime to Instant for consistency with the entity and to ensure that timestamps are stored in UTC format across the application.
}
