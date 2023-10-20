package com.zy.content.api;

import com.zy.content.model.dto.CourseCategoryTreeDto;
import com.zy.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description 课程菜单
 * @date 2023/10/17 16:03
 */
@RestController
public class CourseCategoryController {
    @Autowired
    private CourseCategoryService categoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return categoryService.queryTreeNodes("1");
    }

}
