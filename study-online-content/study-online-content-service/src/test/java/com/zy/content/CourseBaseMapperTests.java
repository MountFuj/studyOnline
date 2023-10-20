package com.zy.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.mapper.CourseBaseMapper;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/17 9:53
 */
@SpringBootTest
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    void testCourseBaseMapper() {
        //分页查询
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1l);
        pageParams.setPageSize(2l);
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        Page<CourseBase> courseBasePage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> basePage = courseBaseMapper.selectPage(courseBasePage, wrapper);
        List<CourseBase> items = basePage.getRecords();
        long total = basePage.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(items,total, pageParams.getPageNo(), pageParams.getPageSize());
        System.out.println(courseBasePageResult);
    }
}
