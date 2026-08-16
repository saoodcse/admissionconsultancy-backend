package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.CourseControllerApi;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CourseDTO;
import com.admissionconsultancy.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CourseController implements CourseControllerApi {

    private final CourseService courseService;

    // PUBLIC ENDPOINTS (No Authentication Required)

    @Override
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getActiveCourses() {
        log.debug("REST request: get all active courses");
        List<CourseDTO> courses = courseService.getActiveCourses();
        return ApiResponse.success(courses, AppCode.COURSE_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseBySlug(@PathVariable("slug") String slug) {
        log.debug("REST request: get course by slug '{}'", slug);
        CourseDTO course = courseService.getBySlug(slug);
        return ApiResponse.success(course, AppCode.COURSE_FETCHED);
    }

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @Override
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCoursesAdmin() {
        log.debug("REST request: get all courses (Admin View)");
        List<CourseDTO> courses = courseService.getAllCoursesAdmin();
        return ApiResponse.success(courses, AppCode.COURSE_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(@RequestBody CourseDTO courseDTO) {
        log.debug("REST request: create new course: '{}'", courseDTO.getName());
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return ApiResponse.success(createdCourse, AppCode.COURSE_CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(@PathVariable("slug") String slug, @RequestBody CourseDTO courseDTO) {
        log.debug("REST request: update course with slug: {}", slug);
        CourseDTO updatedCourse = courseService.updateCourse(slug, courseDTO);
        return ApiResponse.success(updatedCourse, AppCode.COURSE_UPDATED);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable("slug") String slug) {
        log.debug("REST request: soft-delete course with slug: {}", slug);
        courseService.deleteCourse(slug);
        return ApiResponse.success(null, AppCode.COURSE_DELETED);
    }
}