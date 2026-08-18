package com.aegis.operations.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.code(), exception.getMessage());
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransition(InvalidTransitionException exception) {
        return error(HttpStatus.CONFLICT, exception.code(), exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.code(), exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleStorageUnavailable(DataAccessException exception) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "STORAGE_UNAVAILABLE",
                "Operations storage is unavailable.");
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "The request could not be processed.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred.");
    }

    private static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(new ApiError(code, message)));
    }

    public record ApiErrorResponse(ApiError error) {
    }

    public record ApiError(String code, String message) {
    }
}
