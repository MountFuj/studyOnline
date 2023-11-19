package com.zy.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/11/13 19:45
 */
@Data
@ToString
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;
    //课程计划信息
    List<TeachplanDto> teachplans;
    //师资信息暂时不加...
}
