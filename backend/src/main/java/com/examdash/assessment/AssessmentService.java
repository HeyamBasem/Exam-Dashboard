package com.examdash.assessment;

import com.examdash.assessment.dto.AssessmentResponse;
import com.examdash.assessment.dto.CreateAssessmentRequest;
import com.examdash.assessment.dto.UpdateAssessmentRequest;
import com.examdash.assessment.exception.AssessmentNotFoundException;
import com.examdash.auth.exception.AuthenticatedUserNotFoundException;
import com.examdash.common.dto.PagedResponse;
import com.examdash.common.exception.ForbiddenException;
import com.examdash.user.Role;
import com.examdash.user.User;
import com.examdash.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant; //changed to Instant for consistency
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    // changed to use Enum for validation instead of string comparison using a Set
    public enum SortField {
        createdAt, title, subject, grade, totalMarks, durationMinutes
    }

    public AssessmentResponse create(CreateAssessmentRequest request) {
        User currentUser = getAuthenticatedUser();


        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(request.getSubject())
                .grade(request.getGrade())
                .totalMarks(request.getTotalMarks())
                .durationMinutes(request.getDurationMinutes())
                .createdBy(currentUser)
                .build();

        Assessment saved = assessmentRepository.save(assessment);
        return toResponse(saved);
    }

    public AssessmentResponse getById(Long id) {
        Assessment assessment = assessmentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AssessmentNotFoundException(
                        "Assessment not found with id: " + id));
        return toResponse(assessment);
    }

    public PagedResponse<AssessmentResponse> getAll(
            int page, int size, // changed limit to size
            String subject, Integer grade,
            String sortBy, String sortOrder) {

        // changed to use Enum for validation
        try {
            SortField.valueOf(sortBy);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
                            + ". Allowed fields: createdAt, title, subject, grade, totalMarks, durationMinutes");
        }

        // Validate sort order
        if (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException(
                    "Invalid sort order: " + sortOrder
                            + ". Allowed values: asc, desc");
        }

        // Clamp size to valid range
        size = Math.max(1, Math.min(size, 100)); // changed limit to size

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort); // changed limit to size

        // Build dynamic specification
        Specification<Assessment> spec = AssessmentSpecification.isNotDeleted();

        if (subject != null && !subject.isBlank()) {
            spec = spec.and(AssessmentSpecification.hasSubject(subject));
        }

        if (grade != null) {
            spec = spec.and(AssessmentSpecification.hasGrade(grade));
        }

        Page<Assessment> assessmentPage = assessmentRepository.findAll(spec, pageable);

        List<AssessmentResponse> content = assessmentPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<AssessmentResponse>builder()
                .content(content)
                .page(assessmentPage.getNumber())
                .size(assessmentPage.getSize())
                .totalElements(assessmentPage.getTotalElements())
                .totalPages(assessmentPage.getTotalPages())
                .build();
    }

    public AssessmentResponse update(Long id, UpdateAssessmentRequest request) {
        Assessment assessment = assessmentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AssessmentNotFoundException(
                        "Assessment not found with id: " + id));

        User currentUser = getAuthenticatedUser();
        checkModifyPermission(currentUser, assessment);

        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setSubject(request.getSubject());
        assessment.setGrade(request.getGrade());
        assessment.setTotalMarks(request.getTotalMarks());
        assessment.setDurationMinutes(request.getDurationMinutes());

        Assessment updated = assessmentRepository.save(assessment);
        return toResponse(updated);
    }

    public void delete(Long id) {
        Assessment assessment = assessmentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AssessmentNotFoundException(
                        "Assessment not found with id: " + id));

        User currentUser = getAuthenticatedUser();
        checkModifyPermission(currentUser, assessment);

        assessment.setDeletedAt(Instant.now()); //changed to Instant.now() for consistency
        assessmentRepository.save(assessment);
    }

    // ── Authorization helpers ──────────────────────────────────────────

    private void checkModifyPermission(User currentUser, Assessment assessment) {
        // ADMIN can modify any assessment
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        // TEACHER can only modify their own assessments
        if (!assessment.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to modify this assessment");
        }
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Authentication failed"));
    }

    // ── Entity → DTO mapper ────────────────────────────────────────────

    private AssessmentResponse toResponse(Assessment assessment) {
        return AssessmentResponse.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .subject(assessment.getSubject())
                .grade(assessment.getGrade())
                .totalMarks(assessment.getTotalMarks())
                .durationMinutes(assessment.getDurationMinutes())
                .createdBy(assessment.getCreatedBy().getEmail())
                .createdAt(assessment.getCreatedAt())
                .updatedAt(assessment.getUpdatedAt())
                .build();
    }
}