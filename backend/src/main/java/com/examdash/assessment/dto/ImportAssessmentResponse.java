package com.examdash.assessment.dto;

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
@Schema(description = "Preview of a parsed assessment file before confirmation")
public class ImportAssessmentResponse {

    @Schema(description = "Parsed assessment title", example = "Math Quiz Chapter 5")
    private String title;

    @Schema(description = "Original file name", example = "math_quiz.csv")
    private String fileName;

    @Schema(description = "Detected file type", example = "CSV")
    private String fileType;

    @Schema(description = "Total number of questions parsed", example = "10")
    private int totalQuestions;

    @Schema(description = "List of parsed questions")
    private List<QuestionResponse> questions;
}