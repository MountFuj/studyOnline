package com.zy.content;

import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.model.dto.CourseCategoryTreeDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
import com.zy.content.service.CourseBaseInfoService;
import com.zy.content.service.CourseCategoryService;
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
public class CourseCategoryServiceTest {
    @Autowired
    CourseCategoryService categoryService;
    @Test
    void testCategory(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
