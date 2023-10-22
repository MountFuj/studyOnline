package com.zy.content.api;

import com.zy.content.model.po.CourseTeacher;
import com.zy.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/22 19:50
 */
@Api("教师接口")
@RestController
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getAllTeacher(@PathVariable Long courseId){
        return courseTeacherService.getAllTeacher(courseId);
    }
}
