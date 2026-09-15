package com.examdash.assessment.parser;

import com.examdash.assessment.QuestionType;
import com.examdash.assessment.dto.ParsedAssessment;
import com.examdash.assessment.dto.ParsedQuestion;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy: parses CSV files into assessments.
 *
 * Expected CSV format:
 * question,type,option_a,option_b,option_c,option_d,correct_answer
 */
@Component
public class CsvAssessmentParser implements AssessmentParser {

    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_OPTION_A = "option_a";
    private static final String COLUMN_OPTION_B = "option_b";
    private static final String COLUMN_OPTION_C = "option_c";
    private static final String COLUMN_OPTION_D = "option_d";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    @Override
    public boolean supports(String contentType) {
        return contentType != null && (
                contentType.equals("text/csv") ||
                contentType.equals("application/vnd.ms-excel")
        );
    }

    @Override
    public ParsedAssessment parse(MultipartFile file) {
        List<ParsedQuestion> questions = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            validateHeaders(csvParser);

            int rowNumber = 1;
            for (CSVRecord record : csvParser) {
                rowNumber++;
                questions.add(parseRow(record, rowNumber));
            }

        } catch (IllegalArgumentException e) {
            throw e; // re-throw validation errors as-is
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse CSV file: " + e.getMessage());
        }

        if (questions.isEmpty()) {
            throw new IllegalArgumentException("The uploaded CSV file contains no questions");
        }

        // Derive title from filename (without extension)
        String title = deriveTitle(file.getOriginalFilename());

        return ParsedAssessment.builder()
                .title(title)
                .questions(questions)
                .build();
    }

    private void validateHeaders(CSVParser csvParser) {
        var headers = csvParser.getHeaderMap().keySet();

        if (!headers.contains(COLUMN_QUESTION)) {
            throw new IllegalArgumentException(
                    "The uploaded CSV is missing the required 'question' column");
        }
        if (!headers.contains(COLUMN_TYPE)) {
            throw new IllegalArgumentException(
                    "The uploaded CSV is missing the required 'type' column");
        }
        if (!headers.contains(COLUMN_CORRECT_ANSWER)) {
            throw new IllegalArgumentException(
                    "The uploaded CSV is missing the required 'correct_answer' column");
        }
    }

    private ParsedQuestion parseRow(CSVRecord record, int rowNumber) {
        String questionText = record.get(COLUMN_QUESTION);
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException(
                    "Row " + rowNumber + ": question text is required");
        }

        String typeStr = record.get(COLUMN_TYPE);
        QuestionType questionType = parseQuestionType(typeStr, rowNumber);

        String correctAnswer = record.get(COLUMN_CORRECT_ANSWER);
        if (correctAnswer == null || correctAnswer.isBlank()) {
            throw new IllegalArgumentException(
                    "Row " + rowNumber + ": correct answer is required");
        }

        return ParsedQuestion.builder()
                .questionText(questionText.trim())
                .questionType(questionType)
                .optionA(getOptionalColumn(record, COLUMN_OPTION_A))
                .optionB(getOptionalColumn(record, COLUMN_OPTION_B))
                .optionC(getOptionalColumn(record, COLUMN_OPTION_C))
                .optionD(getOptionalColumn(record, COLUMN_OPTION_D))
                .correctAnswer(correctAnswer.trim())
                .build();
    }

    private QuestionType parseQuestionType(String typeStr, int rowNumber) {
        if (typeStr == null || typeStr.isBlank()) {
            throw new IllegalArgumentException(
                    "Row " + rowNumber + ": question type is required");
        }
        try {
            return QuestionType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Row " + rowNumber + ": unsupported question type '" + typeStr
                            + "'. Supported types: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER");
        }
    }

    private String getOptionalColumn(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            return (value != null && !value.isBlank()) ? value.trim() : null;
        } catch (IllegalArgumentException e) {
            return null; // column doesn't exist — that's fine for optional columns
        }
    }

    private String deriveTitle(String filename) {
        if (filename == null || filename.isBlank()) {
            return "Imported Assessment";
        }
        int dotIndex = filename.lastIndexOf('.');
        String name = (dotIndex > 0) ? filename.substring(0, dotIndex) : filename;
        return name.replace('_', ' ').replace('-', ' ');
    }
}