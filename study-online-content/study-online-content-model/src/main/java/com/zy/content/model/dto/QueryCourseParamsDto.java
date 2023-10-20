package com.zy.content.model.dto;
import lombok.Data;
import lombok.ToString;

/**
 * @author Administrator
 * @version 1.0
 * @description 课程查询参数dto
 * @date 2023/10/16 18:59
 */
@Data
@ToString
public class QueryCourseParamsDto{
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
