package com.logistics.platform.common.exceptions;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.common.dto.response.ErrorResponse;
import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("NOT_FOUND")
                .message(ex.getMessage())
                .details(Collections.singletonList("Resource not found"))
                .build();
        return new ResponseEntity<>(ApiResponse.onFailure(error), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(Exception ex) {
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
