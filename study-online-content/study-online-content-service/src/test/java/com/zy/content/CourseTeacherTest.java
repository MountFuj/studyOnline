package com.zy.content;

import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.model.dto.AddCourseDto;
import com.zy.content.model.dto.AddCourseTeacherDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
import com.zy.content.model.po.CourseTeacher;
import com.zy.content.service.CourseBaseInfoService;
import com.zy.content.service.CourseTeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/17 13:56
 */
@SpringBootTest
public class CourseTeacherTest {
    @Autowired
   private CourseTeacherService courseTeacherService;
    @Test
    void testCourseTeacher(){
        List<CourseTeacher> allTeacher = courseTeacherService.getAllTeacher(72L);
        System.out.println(allTeacher);
    }

    @Test
    void testCourseAdd(){
        AddCourseTeacherDto addCourseTeacherDto = new AddCourseTeacherDto();
        addCourseTeacherDto.setCourseId(75L);
        addCourseTeacherDto.setTeacherName("王老师");
        addCourseTeacherDto.setPosition("教师职位");
        addCourseTeacherDto.setIntroduction("教师简介教师简介教师简介教师简介");
        courseTeacherService.saveTeacher(addCourseTeacherDto);
    }
}
