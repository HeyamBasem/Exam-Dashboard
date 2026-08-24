package com.examdash.assessment.parser;

import com.examdash.assessment.QuestionType;
import com.examdash.assessment.dto.ParsedAssessment;
import com.examdash.assessment.dto.ParsedQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CsvAssessmentParserTest {

    private CsvAssessmentParser parser;

    @BeforeEach
    void setUp() {
        parser = new CsvAssessmentParser();
    }

    // ── supports() tests ──────────────────────────────────────────────

    @Test
    void supports_CsvContentType_ReturnsTrue() {
        assertTrue(parser.supports("text/csv"));
    }

    @Test
    void supports_ExcelContentType_ReturnsTrue() {
        assertTrue(parser.supports("application/vnd.ms-excel"));
    }

    @Test
    void supports_PdfContentType_ReturnsFalse() {
        assertFalse(parser.supports("application/pdf"));
    }

    @Test
    void supports_NullContentType_ReturnsFalse() {
        assertFalse(parser.supports(null));
    }

    // ── parse() success tests ─────────────────────────────────────────

    @Test
    void parse_ValidCsv_ReturnsParsedAssessment() {
        String csv = "question,type,option_a,option_b,option_c,option_d,correct_answer\n"
                + "What is 2+2?,MULTIPLE_CHOICE,1,2,3,4,D\n"
                + "Is the sky blue?,TRUE_FALSE,,,,,TRUE\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "math_quiz.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        ParsedAssessment result = parser.parse(file);

        assertNotNull(result);
        assertEquals("math quiz", result.getTitle());
        assertEquals(2, result.getQuestions().size());

        ParsedQuestion q1 = result.getQuestions().get(0);
        assertEquals("What is 2+2?", q1.getQuestionText());
        assertEquals(QuestionType.MULTIPLE_CHOICE, q1.getQuestionType());
        assertEquals("D", q1.getCorrectAnswer());
        assertEquals("1", q1.getOptionA());
    }

    @Test
    void parse_CaseInsensitiveType_ParsesCorrectly() {
        String csv = "question,type,option_a,option_b,option_c,option_d,correct_answer\n"
                + "What is Java?,multiple_choice,Language,Food,Car,Animal,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        ParsedAssessment result = parser.parse(file);
        assertEquals(QuestionType.MULTIPLE_CHOICE, result.getQuestions().get(0).getQuestionType());
    }

    @Test
    void parse_TitleDerivedFromFilename() {
        String csv = "question,type,correct_answer\n"
                + "What is 1+1?,SHORT_ANSWER,2\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "my_cool_test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        ParsedAssessment result = parser.parse(file);
        assertEquals("my cool test", result.getTitle());
    }

    // ── parse() validation error tests ────────────────────────────────

    @Test
    void parse_MissingQuestionColumn_ThrowsException() {
        String csv = "type,correct_answer\n"
                + "MULTIPLE_CHOICE,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().contains("'question' column"));
    }

    @Test
    void parse_MissingTypeColumn_ThrowsException() {
        String csv = "question,correct_answer\n"
                + "What is 2+2?,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().contains("'type' column"));
    }

    @Test
    void parse_EmptyQuestionText_ThrowsException() {
        String csv = "question,type,correct_answer\n"
                + ",MULTIPLE_CHOICE,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().contains("question text is required"));
    }

    @Test
    void parse_InvalidQuestionType_ThrowsException() {
        String csv = "question,type,correct_answer\n"
                + "What is 2+2?,ESSAY,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().contains("unsupported question type"));
    }

    @Test
    void parse_EmptyFile_ThrowsException() {
        String csv = "question,type,correct_answer\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().contains("no questions"));
    }

    @Test
    void parse_MissingCorrectAnswer_ThrowsException() {
        String csv = "question,type,correct_answer\n"
                + "What is 2+2?,MULTIPLE_CHOICE,\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().contains("correct answer is required"));
    }
}