# Week 3 – Backend Architecture & Assessment Management APIs

## Overview

This week, you'll transition from authentication to building the first business feature of the Assessment Dashboard.

Your goal is to understand how to organize backend code using a layered architecture and implement REST APIs that follow engineering best practices.

---

# Learning Objectives

By the end of this week, you should be able to:

- Understand layered backend architecture.
- Separate responsibilities between Controllers, Services, and Repositories.
- Build RESTful CRUD APIs.
- Validate incoming requests.
- Handle errors consistently.
- Design maintainable API endpoints.
- Document APIs using Swagger/OpenAPI.
- Write clean, readable, and maintainable code.

---

# Topics Covered

## Backend Architecture

- Controllers
- Services
- Repositories (or Data Access Layer)
- DTOs
- Request Validation
- Error Handling
- Middleware
- Environment Variables
- Logging

---

# Assignment

Implement Assessment Management APIs.

The Assessment entity should support:

- Create Assessment
- Get Assessment by ID
- Get All Assessments
- Update Assessment
- Delete Assessment (Soft Delete preferred)

---

## Suggested Assessment Model

```text
Assessment

- id
- title
- description
- subject
- grade
- totalMarks
- durationMinutes
- createdBy
- createdAt
- updatedAt
```

Feel free to improve the model if you identify additional useful fields.

---

# API Endpoints

## Create Assessment

POST /api/assessments

---

## Get All Assessments

GET /api/assessments

Support:

- Pagination
- Sorting
- Filtering by Subject
- Filtering by Grade

---

## Get Assessment

GET /api/assessments/:id

---

## Update Assessment

PUT /api/assessments/:id

---

## Delete Assessment

DELETE /api/assessments/:id

---

# Technical Requirements

Your implementation should include:

- Clean folder structure
- Layered architecture
- Validation
- Proper HTTP status codes
- Consistent error responses
- Environment configuration
- Swagger documentation
- Meaningful commit history

---

# Acceptance Criteria

## Functional

- User can create an assessment.
- User can retrieve all assessments.
- User can retrieve a single assessment.
- User can update an assessment.
- User can delete an assessment.
- Invalid requests return validation errors.
- Missing resources return 404.

---

## Technical

- Business logic is not inside controllers.
- Database logic is isolated.
- Validation is reusable.
- APIs follow REST conventions.
- Responses are consistent.
- No duplicated logic.

---

# Bonus Tasks

Choose one or more:

- Search assessments by title.
- Add soft delete.
- Add timestamps.
- Add audit logging.
- Add request logging middleware.
- Add API versioning.
- Implement global error handler.

---

# Deliverables

By Monday, submit:

- Source code
- Pull Request
- Updated README
- Swagger/OpenAPI documentation

---

# Pull Request Checklist

- [ ] Code builds successfully
- [ ] API tested
- [ ] Validation added
- [ ] No console.log left in code
- [ ] Swagger updated
- [ ] Clean commit history
- [ ] PR description completed

---

# Evaluation Rubric

| Category | Weight |
|----------|--------|
| Code Quality | 30% |
| Architecture | 20% |
| API Design | 20% |
| Validation & Error Handling | 10% |
| Documentation | 10% |
| Git & Pull Request Quality | 10% |

---

# Stretch Goal

If you finish early, implement one additional feature of your choice that improves the Assessment API.

Be prepared to explain:
- Why you chose it.
- How you designed it.
- Any trade-offs you made.

---

# Demo Expectations

During the Monday demo, be ready to:

1. Explain your project structure.
2. Walk through one API endpoint.
3. Demonstrate the APIs using Postman or Swagger.
4. Explain one engineering decision you made.
5. Discuss what you would improve if given more time.
