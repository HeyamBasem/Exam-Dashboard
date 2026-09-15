package com.examdash.assessment.dto;

import com.examdash.assessment.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Question details")
public class QuestionResponse {

    @Schema(description = "The question text", example = "What is 2+2?")
    private String questionText;

    @Schema(description = "The type of question", example = "MULTIPLE_CHOICE")
    private QuestionType questionType;

    @Schema(description = "Option A", example = "2")
    private String optionA;

    @Schema(description = "Option B", example = "3")
    private String optionB;

    @Schema(description = "Option C", example = "4")
    private String optionC;

    @Schema(description = "Option D", example = "5")
    private String optionD;

    @Schema(description = "The correct answer", example = "C")
    private String correctAnswer;
}