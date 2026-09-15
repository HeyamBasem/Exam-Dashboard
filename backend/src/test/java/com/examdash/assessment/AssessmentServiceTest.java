package com.examdash.assessment;

import com.examdash.assessment.dto.AssessmentResponse;
import com.examdash.assessment.dto.CreateAssessmentRequest;
import com.examdash.assessment.exception.AssessmentNotFoundException;
import com.examdash.common.exception.ForbiddenException;
import com.examdash.user.Role;
import com.examdash.user.User;
import com.examdash.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    private User mockTeacher;
    private Assessment mockAssessment;

    @BeforeEach
    void setUp() {
        // prepare a mock teacher user
        mockTeacher = User.builder()
                .id(1L)
                .email("teacher@msa.edu.eggg")
                .role(Role.TEACHER)
                .build();

        // prepare a mock assessment
        mockAssessment = Assessment.builder()
                .id(100L)
                .title("Software Engineering Midterm")
                .subject("Computer Engineering")
                .grade(4)
                .totalMarks(50)
                .durationMinutes(90)
                .createdBy(mockTeacher)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // helper method to mock the security context for authenticated user
    private void mockSecurityContext(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, null)
        );
    }

    @Test
    void getById_Success_ReturnsAssessment() {
        // Arrange
        when(assessmentRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(mockAssessment));

        // Act
        AssessmentResponse response = assessmentService.getById(100L);

        // Assert
        assertNotNull(response);
        assertEquals("Software Engineering Midterm", response.getTitle());
        verify(assessmentRepository, times(1)).findByIdAndDeletedAtIsNull(100L);
    }

    @Test
    void getById_NotFound_ThrowsException() {
        // Arrange
        when(assessmentRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AssessmentNotFoundException.class, () -> assessmentService.getById(999L));
    }

    @Test
    void create_Success_ReturnsAssessmentResponse() {
        // Arrange
        mockSecurityContext("teacher@msa.edu.eg");
        when(userRepository.findByEmail("teacher@msa.edu.eg")).thenReturn(Optional.of(mockTeacher));
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(mockAssessment);

        CreateAssessmentRequest request = new CreateAssessmentRequest();
        request.setTitle("Software Engineering Midterm");
        request.setSubject("Computer Engineering");
        request.setGrade(4);
        request.setTotalMarks(50);
        request.setDurationMinutes(90);

        // Act
        AssessmentResponse response = assessmentService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Software Engineering Midterm", response.getTitle());
        verify(assessmentRepository, times(1)).save(any(Assessment.class));
    }

    @Test
    void delete_NotOwnerTeacher_ThrowsForbiddenException() {
        // Arrange: another teacher who is not the owner of the assessment
        User anotherTeacher = User.builder()
                .id(2L)
                .email("other@msa.edu.eg")
                .role(Role.TEACHER)
                .build();

        mockSecurityContext("other@msa.edu.eg");
        when(userRepository.findByEmail("other@msa.edu.eg")).thenReturn(Optional.of(anotherTeacher));
        when(assessmentRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(mockAssessment));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> assessmentService.delete(100L));
        verify(assessmentRepository, never()).save(any(Assessment.class));
    }
}