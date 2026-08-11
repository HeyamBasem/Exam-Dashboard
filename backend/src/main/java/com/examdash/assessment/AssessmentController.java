package com.examdash.assessment;

import com.examdash.assessment.dto.AssessmentResponse;
import com.examdash.assessment.dto.CreateAssessmentRequest;
import com.examdash.assessment.dto.UpdateAssessmentRequest;
import com.examdash.common.dto.ApiResponse;
import com.examdash.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
@Tag(name = "Assessments", description = "Assessment management endpoints")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    @Operation(summary = "Create a new assessment",
            description = "Creates an assessment. Requires authentication. Only ADMIN and TEACHER roles are allowed. STUDENT is forbidden.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Assessment created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Validation failures",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing, invalid, or expired authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role (e.g., STUDENT)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AssessmentResponse>> create(
            @Valid @RequestBody CreateAssessmentRequest request) {
        AssessmentResponse data = assessmentService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assessment created successfully", data));
    }

    @GetMapping
    @Operation(summary = "Get all assessments",
            description = "Retrieves assessments with pagination, filtering by subject/grade, and sorting. All authenticated users can view assessments. Soft-deleted assessments are excluded. Note: An empty filtered list returns a successful 200 response.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid sort field/order or invalid request parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing, invalid, or expired authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<PagedResponse<AssessmentResponse>>> getAll(
            @Parameter(description = "Page number (0-indexed). Must be >= 0.", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size. Typical maximum is 100.", example = "10")
            @RequestParam(defaultValue = "10") int limit,

            @Parameter(description = "Filter by exact subject name", example = "Math")
            @RequestParam(required = false) String subject,

            @Parameter(description = "Filter by exact grade level", example = "10")
            @RequestParam(required = false) Integer grade,

            @Parameter(description = "Sort by field. Allowed values: createdAt, title, subject, grade, totalMarks, durationMinutes", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort order. Allowed values: asc, desc", example = "desc")
            @RequestParam(defaultValue = "desc") String sortOrder) {

        PagedResponse<AssessmentResponse> data = assessmentService.getAll(
                page, limit, subject, grade, sortBy, sortOrder);
        return ResponseEntity.ok(
                ApiResponse.success("Assessments retrieved successfully", data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assessment by ID",
            description = "Retrieves a single active assessment by its ID. Any authenticated user can view an active assessment. Soft-deleted assessments return 404.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success - active assessment found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing, invalid, or expired authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - The assessment does not exist or was soft deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AssessmentResponse>> getById(
            @Parameter(description = "ID of the assessment to retrieve", example = "1") @PathVariable Long id) {
        AssessmentResponse data = assessmentService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Assessment retrieved successfully", data));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an assessment",
            description = "Full replacement update of an assessment. ADMIN can update any assessment. TEACHER can update only assessments they created. STUDENT is forbidden.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success - Assessment updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Validation failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing, invalid, or expired authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role or not the creator",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - Assessment not found or soft deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AssessmentResponse>> update(
            @Parameter(description = "ID of the assessment to update", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateAssessmentRequest request) {
        AssessmentResponse data = assessmentService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Assessment updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an assessment",
            description = "Marks the assessment as deleted (sets deletedAt). The record is not physically removed, but will be excluded from normal GET endpoints. GET by ID for a deleted assessment returns 404. ADMIN can delete any assessment. TEACHER can delete only their own assessment. STUDENT is forbidden.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No Content - Successfully soft deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing, invalid, or expired authentication",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role or not the creator",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - Assessment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the assessment to delete", example = "1") @PathVariable Long id) {
        assessmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
