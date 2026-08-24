package com.examdash.assessment;

import com.examdash.assessment.dto.*;
import com.examdash.assessment.parser.AssessmentParser;
import com.examdash.auth.exception.AuthenticatedUserNotFoundException;
import com.examdash.common.exception.ForbiddenException;
import com.examdash.user.Role;
import com.examdash.user.User;
import com.examdash.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentImportService {

    private final List<AssessmentParser> parsers; // Spring injects ALL implementations
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    
     // Step 1: Parse uploaded file and return a preview (no persistence).
     
    public ImportAssessmentResponse parseFile(MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();

        AssessmentParser parser = parsers.stream()
                .filter(p -> p.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported file type: " + contentType
                                + ". Supported formats: CSV, PDF"));

        ParsedAssessment parsed = parser.parse(file);

        List<QuestionResponse> questionResponses = parsed.getQuestions().stream()
                .map(q -> QuestionResponse.builder()
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .correctAnswer(q.getCorrectAnswer())
                        .build())
                .collect(Collectors.toList());

        return ImportAssessmentResponse.builder()
                .title(parsed.getTitle())
                .fileName(file.getOriginalFilename())
                .fileType(contentType)
                .totalQuestions(questionResponses.size())
                .questions(questionResponses)
                .build();
    }

    
      //Step 2: Confirm and save the previewed assessment with its questions.
     
    @Transactional
    public AssessmentResponse confirmImport(ConfirmImportRequest request) {
        User currentUser = getAuthenticatedUser();

        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(request.getSubject())
                .grade(request.getGrade())
                .totalMarks(request.getTotalMarks())
                .durationMinutes(request.getDurationMinutes())
                .createdBy(currentUser)
                .questions(new java.util.ArrayList<>())
                .build();

        for (QuestionRequest qr : request.getQuestions()) {
            Question question = Question.builder()
                    .questionText(qr.getQuestionText())
                    .questionType(qr.getQuestionType())
                    .optionA(qr.getOptionA())
                    .optionB(qr.getOptionB())
                    .optionC(qr.getOptionC())
                    .optionD(qr.getOptionD())
                    .correctAnswer(qr.getCorrectAnswer())
                    .assessment(assessment)
                    .build();
            assessment.getQuestions().add(question);
        }

        Assessment saved = assessmentRepository.save(assessment);

        return AssessmentResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .subject(saved.getSubject())
                .grade(saved.getGrade())
                .totalMarks(saved.getTotalMarks())
                .durationMinutes(saved.getDurationMinutes())
                .createdBy(currentUser.getEmail())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "The uploaded file exceeds the maximum allowed size of 5 MB");
        }
        if (file.getContentType() == null || file.getContentType().isBlank()) {
            throw new IllegalArgumentException("Unable to determine file type");
        }
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Authentication failed"));
    }
}