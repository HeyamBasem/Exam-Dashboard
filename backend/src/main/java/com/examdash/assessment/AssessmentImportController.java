package com.examdash.assessment;

import com.examdash.assessment.dto.AssessmentResponse;
import com.examdash.assessment.dto.ConfirmImportRequest;
import com.examdash.assessment.dto.ImportAssessmentResponse;
import com.examdash.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assessments/import")
@RequiredArgsConstructor
@Tag(name = "Assessment Import", description = "Import assessments from files (CSV, PDF)")
public class AssessmentImportController {

    private final AssessmentImportService assessmentImportService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and parse an assessment file",
            description = "Uploads a file (CSV or PDF), parses it, and returns a preview of the extracted assessment. "
                    + "Does NOT save to the database. Use the /confirm endpoint to persist.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "File parsed successfully — preview returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Invalid file (wrong type, too large, malformed content)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403",
                    description = "Forbidden — only ADMIN and TEACHER can import",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<ImportAssessmentResponse>> importAssessment(
            @RequestParam("file") MultipartFile file) {
        ImportAssessmentResponse preview = assessmentImportService.parseFile(file);
        return ResponseEntity.ok(
                ApiResponse.success("File parsed successfully. Review and confirm to save.", preview));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping("/confirm")
    @Operation(summary = "Confirm and save an imported assessment",
            description = "Saves the previewed assessment and its questions to the database. "
                    + "The request body should contain the reviewed/edited data from the preview step.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                    description = "Assessment saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Validation failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403",
                    description = "Forbidden — only ADMIN and TEACHER can import",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AssessmentResponse>> confirmImport(
            @Valid @RequestBody ConfirmImportRequest request) {
        AssessmentResponse saved = assessmentImportService.confirmImport(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assessment imported successfully", saved));
    }
}