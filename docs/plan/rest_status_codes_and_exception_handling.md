# RESTful Status Codes & Global Exception Handling Implementation Plan

## Goal
Improve API reliability and client-side error handling by using standard HTTP status codes and a consistent error response format.

## Proposed Changes

### 1. Global Exception Handling
- Create `ErrorResponse` DTO for consistent error structure.
- Create `BusinessException` for domain-specific errors.
- Create `GlobalExceptionHandler` using `@RestControllerAdvice`.

### 2. Status Code Mapping
- **201 Created**: Successful signup.
- **400 Bad Request**: Validation failures.
- **401 Unauthorized**: Authentication failures (wrong password).
- **409 Conflict**: Duplicate resource (already registered email).

### 3. Controller & Service Updates
- Update `AuthController` to use `ResponseEntity.status(HttpStatus.CREATED)` for signup.
- Update `AuthService` to throw specific `BusinessException` types.
