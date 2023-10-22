package com.zy.content;

import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
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
}
