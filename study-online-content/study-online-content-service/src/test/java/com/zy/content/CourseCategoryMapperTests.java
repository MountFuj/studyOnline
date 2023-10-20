package com.zy.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.mapper.CourseBaseMapper;
import com.zy.content.mapper.CourseCategoryMapper;
import com.zy.content.model.dto.CourseCategoryTreeDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
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
public class CourseCategoryMapperTests {
    @Autowired
    CourseCategoryMapper categoryMapper;

    @Test
    void testCourseBaseMapper() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
