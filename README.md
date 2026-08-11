# AssessmentPlatform
Internship Assessment platform

## Features
- **Assessment Management**: Complete CRUD operations for assessments.
- **Advanced Querying**: Pagination, sorting, and filtering by subject and grade.
- **Data Safety**: Soft-delete functionality to preserve records.
- **Security**: Role-based authorization (ADMIN, TEACHER, STUDENT) with JWT authentication.

## API Documentation
The API documentation is powered by Swagger UI and OpenAPI 3.0.

To view and interact with the API documentation:
1. Start the Spring Boot application (`mvn spring-boot:run`).
2. Open your browser and navigate to: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
3. To test protected Assessment endpoints:
   - Use the existing Authentication API (`POST /api/v1/auth/login`) to obtain a JWT.
   - Click the **Authorize** button at the top of the Swagger UI.
   - Paste your JWT token and click Authorize.
   - You can now test the Assessment endpoints securely.
