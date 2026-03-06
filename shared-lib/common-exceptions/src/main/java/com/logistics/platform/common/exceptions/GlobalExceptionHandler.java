package com.logistics.platform.common.exceptions;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.common.dto.response.ErrorResponse;
import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("NOT_FOUND")
                .message(ex.getMessage())
                .details(Collections.singletonList("Resource not found"))
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .details(errors)
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("FORBIDDEN")
                .message("Access Denied")
                .details(Collections.singletonList(ex.getMessage()))
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequestExceptions(RuntimeException ex) {
        log.warn("Bad Request: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("BAD_REQUEST")
                .message(ex.getMessage())
                .details(Collections.singletonList(ex.getMessage()))
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED")
                .message("Authentication Failed")
                .details(Collections.singletonList(ex.getMessage()))
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(Exception ex) {
        String msg = ex.getMessage();
        if (msg != null) {
            if (msg.startsWith("Feature not found:")) {
                ErrorResponse error = ErrorResponse.builder().errorCode("FEATURE_NOT_FOUND").message(msg).build();
                return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.NOT_FOUND);
            }
            if (msg.startsWith("Tenant not found:")) {
                ErrorResponse error = ErrorResponse.builder().errorCode("TENANT_NOT_FOUND").message(msg).build();
                return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.NOT_FOUND);
            }
            if (msg.startsWith("Feature key already exists:")) {
                ErrorResponse error = ErrorResponse.builder().errorCode("FEATURE_ALREADY_EXISTS").message(msg).build();
                return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.CONFLICT);
            }
        }

        log.error("UNEXPECTED ERROR: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .details(Collections.singletonList(ex.getMessage()))
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Fallback for cases where ApiResponse static methods are not fully aligned
    // with this usage yet
    // Assuming ApiResponse has a generic structure, we might need a dedicated
    // failure factory method
    // I will stick to the standard builder pattern if 'onFailure' is not defined.
    // Let's check ApiResponse definition again.
}
