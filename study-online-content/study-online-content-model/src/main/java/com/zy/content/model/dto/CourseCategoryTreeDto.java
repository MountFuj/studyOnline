package com.zy.content.model.dto;

import com.zy.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/17 16:01
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
