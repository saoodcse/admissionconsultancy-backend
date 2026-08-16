package com.admissionconsultancy.dto;

import com.admissionconsultancy.constants.AppCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standardized API response wrapper used across the application")
public final class ApiResponse<T> {

    @Schema(description = "Indicates whether the API request was successful or not", example = "true")
    private boolean success;

    @Schema(description = "Custom application-specific status code", example = "2000")
    private int statuscode;

    @Schema(description = "Human-readable message describing the outcome", example = "Operation completed successfully")
    private String message;

    @Schema(description = "The actual response payload (can be an object, list, or null on error)")
    private T data;

    @Schema(description = "The exact timestamp when the response was generated", example = "2026-08-09T11:12:49")
    private LocalDateTime timestamp;

    // SUCCESS RESPONSE
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, AppCode appCode) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .success(true)
                .statuscode(appCode.getCode())
                .message(appCode.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // ERROR RESPONSE
    public static <T> ResponseEntity<ApiResponse<T>> error(AppCode appCode, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(ApiResponse.<T>builder()
                .success(false)
                .statuscode(appCode.getCode())
                .message(appCode.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // OVERLOADED ERROR (For Dynamic Validation Messages)
    public static <T> ResponseEntity<ApiResponse<T>> error(int statusCode, String customMessage, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(ApiResponse.<T>builder()
                .success(false)
                .statuscode(statusCode)
                .message(customMessage)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> error( int statusCode, String customMessage, T data, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .body(ApiResponse.<T>builder()
                        .success(false)
                        .statuscode(statusCode)
                        .message(customMessage)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build());
    }


}