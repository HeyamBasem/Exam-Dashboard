package com.examdash.assessment.parser;

import com.examdash.assessment.dto.ParsedAssessment;
import org.springframework.web.multipart.MultipartFile;

/**
 * Strategy interface for parsing assessment files.
 *
 * Each implementation handles a specific file format (CSV, PDF, etc.).
 * New formats can be supported by adding a new implementation
 * without modifying existing code (Open/Closed Principle).
 */
public interface AssessmentParser {

    /**
     * Parses the uploaded file and extracts assessment data.
     *
     * @param file the uploaded file
     * @return parsed assessment containing title and questions
     */
    ParsedAssessment parse(MultipartFile file);

    /**
     * Checks whether this parser supports the given content type.
     *
     * @param contentType the MIME type of the uploaded file
     * @return true if this parser can handle the file type
     */
    boolean supports(String contentType);
}