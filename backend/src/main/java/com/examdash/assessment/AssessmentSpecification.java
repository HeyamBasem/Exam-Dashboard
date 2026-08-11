package com.examdash.assessment;

import org.springframework.data.jpa.domain.Specification;

public class AssessmentSpecification {

    private AssessmentSpecification() {
        // Utility class — prevent instantiation
    }

    public static Specification<Assessment> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Assessment> hasSubject(String subject) {
        return (root, query, cb) -> cb.equal(
                cb.lower(root.get("subject")),
                subject.toLowerCase()
        );
    }

    public static Specification<Assessment> hasGrade(Integer grade) {
        return (root, query, cb) -> cb.equal(root.get("grade"), grade);
    }
}
