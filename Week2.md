# Week 2 – High-Level System Design & Authentication

## Overview

This week, you'll begin building the foundation of the Assessment Dashboard.

You'll learn how to think about software architecture before implementation and build the authentication module using industry best practices.

---

# Learning Objectives

By the end of this week, you should be able to:

- Explain High-Level System Design (HLD)
- Understand the architecture of a full-stack application
- Design authentication flows
- Implement user registration
- Implement user login
- Secure passwords
- Protect API endpoints
- Understand JWT authentication

---

# Topics Covered

## High-Level Design

- Client-Server Architecture
- Frontend
- Backend
- Database
- Authentication Flow
- Request Lifecycle

---

## Authentication

- Authentication vs Authorization
- Registration Flow
- Login Flow
- JWT
- Password Hashing
- Protected Routes

---

## Database

Design the initial database schema.

Suggested entities:

- Users
- Roles

Understand:

- Primary Keys
- Relationships
- Constraints

---

# Assignment

Implement the authentication module for the Assessment Dashboard.

---

## Feature 1 – User Registration

The system should allow users to create an account.

Required fields:

- First Name
- Last Name
- Email
- Password

---

## Feature 2 – User Login

Users should be able to authenticate using:

- Email
- Password

Successful authentication should return an access token.

---

## Feature 3 – Protected Route

Create at least one endpoint that requires authentication.

Example:

GET /api/profile

---

# Technical Requirements

Your implementation should include:

- Password hashing
- JWT authentication
- Request validation
- Error handling
- Proper HTTP status codes
- Environment variables

---

# Acceptance Criteria

## Registration

- User can register successfully
- Duplicate email is rejected
- Invalid data returns validation errors
- Password is securely hashed

---

## Login

- Valid credentials return a JWT
- Invalid credentials return Unauthorized
- Sensitive information is never returned

---

## Protected Routes

- Authenticated users can access protected endpoints
- Invalid or expired tokens are rejected

---

# Bonus Tasks

Choose one or more:

- Refresh Tokens
- Logout endpoint
- Email verification (mock implementation)
- Forgot Password flow
- Role field in User model
- Global error handler

---

# Deliverables

Submit:

- Source Code
- Pull Request
- API Documentation
- Database ER Diagram

---

# Pull Request Checklist

- [ ] Registration works
- [ ] Login works
- [ ] Passwords are hashed
- [ ] JWT implemented
- [ ] Validation added
- [ ] API documented
- [ ] Clean commit history

---

# Evaluation Rubric

| Category | Weight |
|----------|--------|
| Authentication | 30% |
| Security | 20% |
| API Design | 20% |
| Code Quality | 15% |
| Documentation | 15% |

---

# Demo Expectations

Be prepared to:

1. Explain your authentication flow.
2. Demonstrate registration.
3. Demonstrate login.
4. Show a protected endpoint.
5. Explain how JWT works.
6. Explain where passwords are stored and why they are secure.

---

# End of Week Reflection

Answer the following questions:

1. What part of authentication was the most challenging?
2. How does JWT authentication work?
3. Why should passwords never be stored as plain text?
4. If this application had one million users, what improvements would you make to the authentication system?
