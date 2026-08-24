package com.examdash.assessment;

import com.examdash.assessment.dto.ImportAssessmentResponse;
import com.examdash.assessment.parser.AssessmentParser;
import com.examdash.assessment.parser.CsvAssessmentParser;
import com.examdash.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AssessmentImportServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private UserRepository userRepository;

    private AssessmentImportService importService;

    @BeforeEach
    void setUp() {
        // Inject real CsvAssessmentParser to test parser selection
        List<AssessmentParser> parsers = List.of(new CsvAssessmentParser());
        importService = new AssessmentImportService(parsers, assessmentRepository, userRepository);
    }

    // ── Parser selection tests ────────────────────────────────────────

    @Test
    void parseFile_CsvFile_SelectsCsvParser() {
        String csv = "question,type,correct_answer\n"
                + "What is 2+2?,MULTIPLE_CHOICE,C\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        ImportAssessmentResponse response = importService.parseFile(file);

        assertNotNull(response);
        assertEquals("text/csv", response.getFileType());
        assertEquals(1, response.getTotalQuestions());
    }

    @Test
    void parseFile_UnsupportedType_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.json", "application/json",
                "{}".getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> importService.parseFile(file));
        assertTrue(ex.getMessage().contains("Unsupported file type"));
    }

    // ── File validation tests ─────────────────────────────────────────

    @Test
    void parseFile_EmptyFile_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv", new byte[0]);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> importService.parseFile(file));
        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    void parseFile_FileTooLarge_ThrowsException() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6 MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "huge.csv", "text/csv", largeContent);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> importService.parseFile(file));
        assertTrue(ex.getMessage().contains("maximum allowed size"));
    }

    @Test
    void parseFile_NullContentType_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", null,
                "data".getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> importService.parseFile(file));
        assertTrue(ex.getMessage().contains("Unable to determine file type"));
    }

    // ── Integration: parser + service ─────────────────────────────────

    @Test
    void parseFile_ValidCsv_ReturnsCorrectPreview() {
        String csv = "question,type,option_a,option_b,option_c,option_d,correct_answer\n"
                + "What is 2+2?,MULTIPLE_CHOICE,1,2,3,4,D\n"
                + "Is Earth flat?,TRUE_FALSE,,,,,FALSE\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "science_quiz.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        ImportAssessmentResponse response = importService.parseFile(file);

        assertEquals("science quiz", response.getTitle());
        assertEquals("science_quiz.csv", response.getFileName());
        assertEquals(2, response.getTotalQuestions());
        assertEquals("What is 2+2?", response.getQuestions().get(0).getQuestionText());
    }
}