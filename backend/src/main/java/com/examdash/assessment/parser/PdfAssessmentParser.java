package com.examdash.assessment.parser;

import com.examdash.assessment.QuestionType;
import com.examdash.assessment.dto.ParsedAssessment;
import com.examdash.assessment.dto.ParsedQuestion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy: parses PDF files into assessments.
 * Proves the Open/Closed Principle.
 */
@Component
public class PdfAssessmentParser implements AssessmentParser {

    @Override
    public boolean supports(String contentType) {
        return "application/pdf".equals(contentType);
    }

    @Override
    public ParsedAssessment parse(MultipartFile file) {
        String text;
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read PDF file: " + e.getMessage());
        }

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("The PDF file contains no readable text");
        }

        List<ParsedQuestion> questions = extractQuestions(text);

        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Could not find any correctly formatted questions in the PDF");
        }

        return ParsedAssessment.builder()
                .title(deriveTitle(file.getOriginalFilename()))
                .questions(questions)
                .build();
    }

    private List<ParsedQuestion> extractQuestions(String text) {
        List<ParsedQuestion> questions = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");

        ParsedQuestion.ParsedQuestionBuilder currentQuestion = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String lowerLine = line.toLowerCase();
            if (lowerLine.startsWith("question:")) {
                if (currentQuestion != null && isValid(currentQuestion.build())) {
                    questions.add(currentQuestion.build());
                }
                currentQuestion = ParsedQuestion.builder();
                currentQuestion.questionText(line.substring(9).trim());
            } else if (currentQuestion != null) {
                if (lowerLine.startsWith("type:")) {
                    currentQuestion.questionType(parseType(line.substring(5).trim()));
                } else if (lowerLine.startsWith("a:")) {
                    currentQuestion.optionA(line.substring(2).trim());
                } else if (lowerLine.startsWith("b:")) {
                    currentQuestion.optionB(line.substring(2).trim());
                } else if (lowerLine.startsWith("c:")) {
                    currentQuestion.optionC(line.substring(2).trim());
                } else if (lowerLine.startsWith("d:")) {
                    currentQuestion.optionD(line.substring(2).trim());
                } else if (lowerLine.startsWith("answer:")) {
                    currentQuestion.correctAnswer(line.substring(7).trim());
                }
            }
        }

        // Add the last question
        if (currentQuestion != null && isValid(currentQuestion.build())) {
            questions.add(currentQuestion.build());
        }

        return questions;
    }

    private QuestionType parseType(String typeStr) {
        try {
            return QuestionType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Will be caught by isValid()
        }
    }

    private boolean isValid(ParsedQuestion q) {
        return q.getQuestionText() != null && !q.getQuestionText().isEmpty()
                && q.getQuestionType() != null
                && q.getCorrectAnswer() != null && !q.getCorrectAnswer().isEmpty();
    }

    private String deriveTitle(String filename) {
        if (filename == null || filename.isBlank()) return "Imported Assessment";
        int dotIndex = filename.lastIndexOf('.');
        String name = (dotIndex > 0) ? filename.substring(0, dotIndex) : filename;
        return name.replace('_', ' ').replace('-', ' ');
    }
}
