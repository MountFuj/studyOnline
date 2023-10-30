package com.zy.content.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/22 20:06
 */
@Data
@ToString
public class AddCourseTeacherDto {
    @TableField("id")
    private Long id;
    private Long courseId;
    @NotEmpty(message = "教师姓名不能为空")
    private String teacherName;
    @NotEmpty(message = "教师职位不能为空")
    private String position;
    @NotEmpty(message = "教师简介不能为空")
    @Size(message = "教师简介不能少于10个字",min = 10)
    private String introduction;
}
