package com.admissionconsultancy.exception;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Hidden // Prevents Swagger from incorrectly interpreting this advice class as a controller endpoint
@Slf4j
@RestControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Handles our custom application exceptions (e.g., ResourceNotFound, DuplicateResource).
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        log.warn("Application Exception [{}]: {}", ex.getAppCode().name(), ex.getMessage());
        return ApiResponse.error(ex.getAppCode().getCode(), ex.getMessage(), ex.getHttpStatus());
    }

    /**
     * Handles @Valid validation failures from DTOs.
     * Extracts all field errors and joins them into a single readable string.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                FieldError::getDefaultMessage,
                                Collectors.toList()
                        )
                ));

        log.warn("Validation Errors: {}", errors);

        return ApiResponse.error(
                AppCode.BAD_REQUEST.getCode(),
                "Invalid request",
                errors,
                HttpStatus.BAD_REQUEST
        );
    }



    /**
     * Handles malformed JSON bodies in the request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return ApiResponse.error(AppCode.BAD_REQUEST.getCode(), "Malformed JSON request format.", HttpStatus.BAD_REQUEST);
    }

    /**
     * Fix for the favicon.ico and '/' missing static resource browser errors.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Endpoint or static resource not found: /{}", ex.getResourcePath());
        return ApiResponse.error(AppCode.RESOURCE_NOT_FOUND.getCode(), "The requested endpoint or resource was not found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Handles incorrect URL parameters (e.g., expecting an Integer ID but got a String).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Parameter '%s' should be of type '%s'",
                ex.getName(), ex.getRequiredType().getSimpleName());

        log.warn("Type Mismatch Error: {}", errorMessage);
        return ApiResponse.error(AppCode.BAD_REQUEST.getCode(), errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Spring Security Authentication Failures (401).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failure: {}", ex.getMessage());
        return ApiResponse.error(AppCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles Spring Security Access Denied Failures (403 - User is logged in but lacks permissions).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ApiResponse.error(
                AppCode.UNAUTHORIZED.getCode(),
                "You do not have permission to perform this action.",
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Catch-All for any unhandled server errors (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("An unexpected internal error occurred: ", ex);
        return ApiResponse.error(AppCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}