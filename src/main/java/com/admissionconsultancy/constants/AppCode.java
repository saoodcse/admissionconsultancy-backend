package com.admissionconsultancy.constants;

import lombok.Getter;

/**
 * Standardized Application Response Codes and Messages.
 * Design Pattern: Enum for Type Safety and Centralized Constants.
 */
@Getter
public enum AppCode {

    // GLOBAL CODES
     
    SUCCESS(6000, "Operation successful"),
    BAD_REQUEST(4000, "Bad request or validation failure"),
    UNAUTHORIZED(8003, "Unauthorized access. Please log in."),
    INTERNAL_SERVER_ERROR(9000, "Something went wrong. Please try again later."),
    RESOURCE_NOT_FOUND(8000, "The requested resource could not be found"),

     
    // COUNTRY API CODES (600X)
     
    COUNTRY_FETCHED(6000, "Countries retrieved successfully"),
    COUNTRY_CREATED(6001, "Country created successfully"),
    COUNTRY_UPDATED(6002, "Country updated successfully"),
    COUNTRY_DELETED(6003, "Country deleted successfully"),
    COUNTRY_NOT_FOUND(6004, "Country not found with the provided details"),
    COUNTRY_ALREADY_EXISTS(6005, "A country with this slug already exists"),

     
    // COURSE API CODES (700X)
     
    COURSE_FETCHED(6000, "Courses retrieved successfully"),
    COURSE_CREATED(7001, "Course created successfully"),
    COURSE_UPDATED(7002, "Course updated successfully"),
    COURSE_DELETED(7003, "Course deleted successfully"),
    COURSE_NOT_FOUND(7004, "Course not found with the provided details"),

     
    // UNIVERSITY API CODES (710X)
     
    UNIVERSITY_FETCHED(6000, "Universities retrieved successfully"),
    UNIVERSITY_CREATED(7101, "University created successfully"),
    UNIVERSITY_UPDATED(7102, "University updated successfully"),
    UNIVERSITY_DELETED(7103, "University deleted successfully"),
    UNIVERSITY_NOT_FOUND(7104, "University not found with the provided details"),

     
    // COLLEGE API CODES (720X)
     
    COLLEGE_FETCHED(6000, "Colleges retrieved successfully"),
    COLLEGE_CREATED(7201, "College created successfully"),
    COLLEGE_UPDATED(7202, "College updated successfully"),
    COLLEGE_DELETED(7203, "College deleted successfully"),
    COLLEGE_NOT_FOUND(7204, "College not found with the provided details"),

     
    // LEAD API CODES (730X)
     
    LEAD_FETCHED(6000, "Leads retrieved successfully"),
    LEAD_CREATED(7301, "Lead submitted successfully"),
    LEAD_UPDATED(7302, "Lead status updated successfully"),
    LEAD_DELETED(7303, "Lead deleted successfully"),
    LEAD_NOT_FOUND(7304, "Lead not found with the provided ID"),



     
    // BLOG API CODES (740X)
     
    BLOG_FETCHED(6000, "Blogs retrieved successfully"),
    BLOG_CREATED(7401, "Blog created successfully"),
    BLOG_UPDATED(7402, "Blog updated successfully"),
    BLOG_DELETED(7403, "Blog deleted successfully"),
    BLOG_NOT_FOUND(7404, "Blog not found with the provided details"),

     
    // AUTH & SECURITY CODES (750X)
     
    LOGIN_SUCCESS(7500, "Login successful"),
    REGISTER_SUCCESS(7501, "User registered successfully"),
    INVALID_CREDENTIALS(7502, "Invalid email or password");

    private final int code;
    private final String message;

    AppCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
