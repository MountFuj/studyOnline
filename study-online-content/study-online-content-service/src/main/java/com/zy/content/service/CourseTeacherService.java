package com.zy.content.service;

import com.zy.content.model.dto.AddCourseTeacherDto;
import com.zy.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    public List<CourseTeacher> getAllTeacher(Long courseId);
    public CourseTeacher saveTeacher(AddCourseTeacherDto addCourseTeacherDto);
    public void deleteTeacher(Long courseId,Long id);
}
