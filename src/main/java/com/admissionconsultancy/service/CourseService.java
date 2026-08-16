package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.CourseDTO;
import java.util.List;

public interface CourseService {
    List<CourseDTO> getActiveCourses();
    List<CourseDTO> getAllCoursesAdmin();
    CourseDTO getBySlug(String slug);
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(String slug, CourseDTO courseDTO);
    void deleteCourse(String slug);
}