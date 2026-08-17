# Week 4 – SOLID Principles, Design Patterns & Document-Based Assessments

## Overview

This week focuses on two important areas of professional software engineering:

1. Writing maintainable and extensible code using **SOLID principles and design patterns**.
2. Building a real business feature for the Assessment Dashboard: allowing teachers to create assessments from uploaded files.

You will apply the concepts you learn directly to the existing Assessment Dashboard codebase.

The goal is not only to make the feature work, but also to design it in a way that makes it easy to support additional assessment formats in the future.

---

# Learning Objectives

By the end of this week, you should be able to:

- Explain the five SOLID principles.
- Identify violations of SOLID principles in existing code.
- Understand common software design patterns.
- Understand when a design pattern should and should not be used.
- Apply at least one design pattern to a real feature.
- Design an extensible file-processing architecture.
- Implement file upload functionality on the frontend.
- Implement file processing APIs on the backend.
- Handle different file formats without duplicating business logic.
- Validate uploaded files.
- Handle upload and processing errors.
- Write maintainable and testable code.

---

# Part 1 – SOLID Principles

Study and understand the following principles.

## 1. Single Responsibility Principle

A class/module should have one reason to change.

Consider:

```text
AssessmentService
    ├── Validate assessment
    ├── Parse PDF
    ├── Parse CSV
    └── Save assessment
```

Discuss why this could become difficult to maintain.

---

## 2. Open/Closed Principle

Software entities should be:

- Open for extension
- Closed for modification

Think about how this principle applies when adding a new assessment file format.

For example:

```text
Today:
PDF
CSV
Document

Future:
Excel
JSON
Word
```

Adding a new format should not require rewriting the existing assessment processing logic.

---

## 3. Liskov Substitution Principle

Understand how different implementations can be substituted without breaking the expected behavior of the application.

Apply this concept to your file-processing implementations.

---

## 4. Interface Segregation Principle

Avoid forcing a class to depend on methods it does not need.

Think about whether all assessment processors should be required to implement the exact same functionality.

---

## 5. Dependency Inversion Principle

High-level business logic should not depend directly on low-level implementation details.

For example:

```text
Assessment Service
        ↓
File Processor Interface
        ↓
PDF / CSV / Document implementation
```

rather than:

```text
Assessment Service
        ↓
PDFParser
```

---

# Part 2 – Design Patterns

Study the following patterns:

### Creational

- Factory Pattern

### Structural

- Adapter Pattern

### Behavioral

- Strategy Pattern

You should understand:

- What problem each pattern solves.
- When to use it.
- When not to use it.
- Advantages and disadvantages.
- How it affects maintainability and testability.

---

# Required Design Pattern

## Strategy Pattern

For this week's implementation, apply the **Strategy Pattern** to assessment file processing.

The system should support multiple assessment formats.

Example:

```text
AssessmentImportService
            |
            v
    AssessmentParser
            |
      +-----+-----+
      |     |     |
     PDF   CSV  Document
```

Each parser should implement a common contract.

For example:

```text
AssessmentParser

+ parse(file)
+ validate(file)
```

The exact implementation is up to you.

---

# Part 3 – Assessment File Upload

## Business Requirement

Teachers should be able to create an assessment by uploading an assessment file.

The initial supported formats are:

- PDF
- CSV
- Document

The system should determine the file type and use the appropriate processing strategy.

---

# User Story

> As a teacher, I want to upload an assessment file so that I can create an assessment without manually entering all of the questions.

---

# Functional Requirements

## Upload Assessment

The teacher should be able to:

1. Open the "Create Assessment" page.
2. Select an assessment file.
3. Upload the file.
4. See the upload/processing status.
5. See validation errors if the file is invalid.
6. Preview the extracted assessment information.
7. Confirm the assessment.
8. Save the assessment.

---

# Supported Formats

## PDF

The system should accept PDF assessment files.

The backend should extract the relevant assessment information from the PDF.

At minimum, extract:

- Assessment title
- Questions
- Question type
- Options where applicable
- Correct answer where available

---

## CSV

The system should accept CSV files using an agreed structure.

Example:

```csv
question,type,option_a,option_b,option_c,option_d,correct_answer
What is 2+2?,multiple_choice,2,3,4,5,C
What is the capital of France?,multiple_choice,London,Paris,Berlin,Rome,B
```

The implementation should validate:

- Required columns
- Empty values
- Supported question types
- Correct answer format

---

## Document

The system should accept supported document files.

The implementation should define:

- Supported document format(s)
- Expected document structure
- Parsing rules
- Validation rules

For example:

```text
Question: What is 2 + 2?
A. 2
B. 3
C. 4
D. 5
Answer: C
```

---

# Backend Requirements

Create an endpoint similar to:

```http
POST /api/assessments/import
```

The endpoint should:

1. Authenticate the teacher.
2. Validate the uploaded file.
3. Determine the file type.
4. Select the appropriate parser.
5. Parse the file.
6. Validate the extracted assessment.
7. Return the parsed assessment.
8. Save the assessment after confirmation.

---

# Suggested Architecture

A possible architecture:

```text
Controller
    |
    v
Assessment Import Service
    |
    v
Parser Factory / Strategy Selection
    |
    +-------- PDF Parser
    |
    +-------- CSV Parser
    |
    +-------- Document Parser
    |
    v
Assessment Validation
    |
    v
Assessment Repository
    |
    v
Database
```

You are not required to follow this exact structure.

You should be able to explain and justify your architecture.

---

# Frontend Requirements

Create an assessment import page.

The page should include:

- File upload component
- Supported file types
- File size validation
- Upload button
- Loading state
- Processing state
- Error state
- Assessment preview
- Confirmation action

Example flow:

```text
Select File
     ↓
Upload
     ↓
Processing
     ↓
Validation
     ↓
Preview
     ↓
Confirm
     ↓
Assessment Created
```

---

# Validation

The system should validate both the file and its contents.

## File Validation

Validate:

- File type
- File extension
- File size
- Empty files

---

## Assessment Validation

Validate:

- Assessment title
- Questions
- Question type
- Required fields
- Answer format
- Duplicate questions where applicable

---

# Error Handling

The application should provide meaningful errors.

Examples:

```text
Unsupported file type.

The uploaded CSV is missing the required "question" column.

The assessment contains an invalid question format.

The uploaded file exceeds the maximum allowed size.

Unable to process the uploaded document.
```

Avoid exposing internal errors or stack traces to the user.

---

# Security Considerations

Consider the security implications of file uploads.

Research and discuss:

- Malicious files
- File type spoofing
- File size limits
- Path traversal
- Virus/malware scanning
- Storing uploaded files safely
- Authentication and authorization

You do not need to implement enterprise-grade malware scanning unless the project infrastructure supports it, but you should document how it would be handled in production.

---

# Testing Requirements

Add tests for:

## Backend

- PDF processing
- CSV processing
- Invalid file type
- Invalid file contents
- Missing required fields
- Unauthorized request
- Parser selection

## Frontend

- File selection
- Validation errors
- Upload state
- Processing state
- Successful preview
- Failed upload
- Confirmation

---

# SOLID Application Task

You must identify one part of the existing codebase that can be improved using SOLID principles.

Document:

### Before

Explain:

- What problem exists?
- Which SOLID principle is violated?
- Why is the current implementation difficult to maintain?

### After

Explain:

- What did you change?
- Which principle did you apply?
- How does the new implementation improve the code?

---

# Design Pattern Application Task

Implement the Strategy Pattern for assessment file processing.

Your implementation should allow:

```text
PDF → PDF Parser
CSV → CSV Parser
Document → Document Parser
```

without requiring the main assessment service to contain format-specific parsing logic.

---

# Acceptance Criteria

## Assessment Upload

- [ ] Teacher can upload an assessment file.
- [ ] PDF files are supported.
- [ ] CSV files are supported.
- [ ] Document files are supported.
- [ ] Unsupported files are rejected.
- [ ] File size is validated.
- [ ] Invalid files return meaningful errors.

## Assessment Processing

- [ ] The correct parser is selected based on the file type.
- [ ] Assessment information is extracted.
- [ ] Extracted data is validated.
- [ ] Teacher can preview the extracted assessment.
- [ ] Teacher can confirm the assessment.
- [ ] Confirmed assessments are stored in the database.

## Frontend

- [ ] Upload UI is implemented.
- [ ] Loading state is displayed.
- [ ] Processing state is displayed.
- [ ] Validation errors are displayed.
- [ ] Processing errors are displayed.
- [ ] Assessment preview is implemented.
- [ ] Successful assessment creation is displayed to the teacher.

## Architecture

- [ ] SOLID principles are applied.
- [ ] At least one SOLID refactoring is documented.
- [ ] Strategy Pattern is implemented.
- [ ] Parser implementations are separated.
- [ ] Business logic is not placed inside controllers.
- [ ] No duplicated format-specific logic exists in the main assessment service.

## Testing

- [ ] Backend tests are implemented.
- [ ] Frontend tests are implemented.
- [ ] Invalid files are tested.
- [ ] Parser selection is tested.
- [ ] Authentication/authorization is tested.

---

# Deliverables

By the end of the week, submit:

1. Backend implementation.
2. Frontend implementation.
3. Database changes/migrations.
4. Unit tests.
5. API documentation.
8. Pull Request.

---

# Architecture Diagram

Add an architecture diagram showing:

```text
Frontend
   |
   v
API
   |
   v
Assessment Import Service
   |
   +---- PDF Parser
   |
   +---- CSV Parser
   |
   v
Validation
   |
   v
Repository
   |
   v
Database
```

You may use:

- Mermaid
- Draw.io
- Excalidraw
- Figma

---

# Pull Request Checklist

- [ ] Feature works locally.
- [ ] Frontend implemented.
- [ ] Backend implemented.
- [ ] Database updated.
- [ ] Authentication enforced.
- [ ] Validation implemented.
- [ ] Error handling implemented.
- [ ] Tests added.
- [ ] SOLID principle applied.
- [ ] Design pattern implemented.
- [ ] Architecture diagram added.
- [ ] API documentation updated.
- [ ] README updated.
- [ ] No unnecessary code duplication.
- [ ] No debug logs left in production code.
- [ ] PR description explains the implementation and design decisions.

---

# Demo Expectations

During the weekly demo, be prepared to:

1. Demonstrate uploading a PDF assessment.
2. Demonstrate uploading a CSV assessment.
3. Show the extracted assessment preview.
4. Demonstrate validation errors.
5. Explain the backend architecture.
6. Explain the Strategy Pattern implementation.
7. Show where SOLID principles were applied.
8. Walk through one important test.
9. Explain one technical trade-off you made.

---

# Discussion Questions

## SOLID

1. Which SOLID principle did you find most useful?
2. Where did you identify a violation in the existing code?
3. Can following SOLID too strictly make a codebase unnecessarily complex?

## Design Patterns

1. Why did we use the Strategy Pattern?
2. Why shouldn't the controller directly parse PDFs and CSV files?
3. What would you need to change to support Excel files?
4. When would a Factory Pattern be useful here?
5. When would using a design pattern be over-engineering?

## Architecture

1. What happens when a teacher uploads a file?
2. Where should file validation happen?
3. Where should parsing happen?
4. Where should business validation happen?
5. What would change if file processing became slow?

---

# Stretch Goals

If you finish the core requirements early, consider:

- Add Excel support.
- Add upload progress.
- Add background processing for large files.

---

# End of Week Reflection

Answer the following:

1. Which SOLID principle changed the way you think about code?
2. Why is the Strategy Pattern useful for this feature?
3. What would happen if we added 10 more assessment formats without using an extensible architecture?
4. What was the most challenging part of implementing file processing?
5. What would you change in your implementation if this were going to production?
6. What did you learn from the code review process?
