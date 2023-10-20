package com.zy.content;

import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
import com.zy.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/17 13:56
 */
@SpringBootTest
public class CourseBaseInfoTest {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Test
    void testBaseInfo(){
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1l);
        pageParams.setPageSize(2l);
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }
}
