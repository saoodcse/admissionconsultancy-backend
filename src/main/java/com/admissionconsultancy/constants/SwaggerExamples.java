package com.admissionconsultancy.constants;

/**
 * Common Swagger API Response Examples used across all controllers.
 * Keeps documentation consistent and ControllerApi interfaces clean.
 */
public final class SwaggerExamples {

    private SwaggerExamples() {
        // Restrict instantiation
    }

    // ==========================================
    // 2XX SUCCESS RESPONSES
    // ==========================================

    // (Note: For GET/POST returning DTOs, Swagger auto-generates the 200/201 schemas from your DTOs.
    // This is useful for DELETE or generic void endpoints).
    public static final String SUCCESS_DELETED = """
            {
              "success": true,
              "statuscode": 2003,
              "message": "Resource deleted successfully",
              "data": null,
              "timestamp": "2026-08-09T12:20:00.0000000"
            }
            """;

    // ==========================================
    // 4XX CLIENT ERRORS
    // ==========================================

    public static final String BAD_REQUEST_VALIDATION = """
            {
              "success": false,
              "statuscode": 4000,
              "message": "Invalid request",
              "data": {
                "name": [
                  "Name cannot be blank"
                ],
                "slug": [
                  "Slug cannot be blank"
                ]
              },
              "timestamp": "2026-08-09T12:17:53.5390037"
            }
            """;

    public static final String UNAUTHORIZED = """
            {
              "success": false,
              "statuscode": 8003,
              "message": "Invalid email or password",
              "data": null,
              "timestamp": "2026-08-09T11:49:22.6311958"
            }
            """;

    public static final String FORBIDDEN = """
            {
              "success": false,
              "statuscode": 8004,
              "message": "You do not have permission to perform this action.",
              "data": null,
              "timestamp": "2026-08-09T11:49:50.0000000"
            }
            """;

    public static final String NOT_FOUND = """
            {
              "success": false,
              "statuscode": 4004,
              "message": "The requested resource was not found",
              "data": null,
              "timestamp": "2026-08-09T11:50:00.0000000"
            }
            """;

    public static final String CONFLICT = """
            {
              "success": false,
              "statuscode": 6005,
              "message": "A resource with this identifier already exists",
              "data": null,
              "timestamp": "2026-08-09T11:50:30.0886685"
            }
            """;

    // ==========================================
    // 5XX SERVER ERRORS
    // ==========================================

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "statuscode": 5000,
              "message": "An unexpected internal error occurred",
              "data": null,
              "timestamp": "2026-08-09T12:00:00.0000000"
            }
            """;
}