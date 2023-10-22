package com.zy.content.service;

import com.zy.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    public List<CourseTeacher> getAllTeacher(Long courseId);
}
