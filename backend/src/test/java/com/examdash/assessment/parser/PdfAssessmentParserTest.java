package com.examdash.assessment.parser;

import com.examdash.assessment.QuestionType;
import com.examdash.assessment.dto.ParsedAssessment;
import com.examdash.assessment.dto.ParsedQuestion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PdfAssessmentParserTest {

    private PdfAssessmentParser parser;

    @BeforeEach
    void setUp() {
        parser = new PdfAssessmentParser();
    }

    @Test
    void supports_PdfContentType_ReturnsTrue() {
        assertTrue(parser.supports("application/pdf"));
    }

    @Test
    void supports_CsvContentType_ReturnsFalse() {
        assertFalse(parser.supports("text/csv"));
    }

    @Test
    void parse_ValidPdf_ReturnsParsedAssessment() throws Exception {
        // Create a real, in-memory PDF using PDFBox to test extraction
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(25, 700);
                contentStream.setLeading(14.5f);
                
                contentStream.showText("Question: What is 2+2?");
                contentStream.newLine();
                contentStream.showText("Type: MULTIPLE_CHOICE");
                contentStream.newLine();
                contentStream.showText("A: 1");
                contentStream.newLine();
                contentStream.showText("B: 2");
                contentStream.newLine();
                contentStream.showText("C: 3");
                contentStream.newLine();
                contentStream.showText("D: 4");
                contentStream.newLine();
                contentStream.showText("Answer: D");
                contentStream.endText();
            }
            document.save(baos);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", "math_test.pdf", "application/pdf", baos.toByteArray());

        ParsedAssessment result = parser.parse(file);

        assertNotNull(result);
        assertEquals("math test", result.getTitle());
        assertEquals(1, result.getQuestions().size());

        ParsedQuestion q1 = result.getQuestions().get(0);
        assertEquals("What is 2+2?", q1.getQuestionText());
        assertEquals(QuestionType.MULTIPLE_CHOICE, q1.getQuestionType());
        assertEquals("1", q1.getOptionA());
        assertEquals("D", q1.getCorrectAnswer());
    }

    @Test
    void parse_EmptyFile_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> parser.parse(file));
    }
}
