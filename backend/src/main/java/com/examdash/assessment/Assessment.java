package com.examdash.assessment;

import com.examdash.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;  // Import the Instant class for timestamp handling instead of LocalDateTime

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(nullable = false)
    private Integer grade;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

// changed LocalDateTime to Instant for deletedAt, createdAt, and updatedAt fields
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

// changed LocalDateTime to Instant here for the onCreate and onUpdate methods
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

// changed LocalDateTime to Instant here also for the updatedAt field
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
