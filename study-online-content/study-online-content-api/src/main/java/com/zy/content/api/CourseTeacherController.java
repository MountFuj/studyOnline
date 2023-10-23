package com.zy.content.api;

import com.zy.content.model.dto.AddCourseTeacherDto;
import com.zy.content.model.po.CourseTeacher;
import com.zy.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("查询教师列表")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getAllTeacher(@PathVariable Long courseId){
        return courseTeacherService.getAllTeacher(courseId);
    }
    @ApiOperation("新增教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody AddCourseTeacherDto addCourseTeacherDto){
        return courseTeacherService.saveTeacher(addCourseTeacherDto);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long id){
        courseTeacherService.deleteTeacher(courseId,id);
    }
}
