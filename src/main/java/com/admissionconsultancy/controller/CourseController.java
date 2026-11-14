package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CourseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface CourseController {

    // PUBLIC ENDPOINTS (No Authentication Required)

    @GetMapping("/courses")
    ResponseEntity<ApiResponse<List<CourseDTO>>> getActiveCourses();

    @GetMapping("/courses/{slug}")
    ResponseEntity<ApiResponse<CourseDTO>> getCourseBySlug(@PathVariable("slug") String slug);

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @GetMapping("/admin/courses/all")
    ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCoursesAdmin();

    @PostMapping("/admin/courses")
    ResponseEntity<ApiResponse<CourseDTO>> createCourse(@Valid @RequestBody CourseDTO courseDTO);

    @PutMapping("/admin/courses/{slug}")
    ResponseEntity<ApiResponse<CourseDTO>> updateCourse(
            @PathVariable("slug") String slug,
            @Valid @RequestBody CourseDTO courseDTO
    );

    @DeleteMapping("/admin/courses/{slug}")
    ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable("slug") String slug);
}