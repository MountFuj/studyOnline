package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.base.exception.StudyOnlineException;
import com.zy.content.mapper.CourseTeacherMapper;
import com.zy.content.model.dto.AddCourseTeacherDto;
import com.zy.content.model.po.CourseTeacher;
import com.zy.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/22 19:53
 */
@Service
@Slf4j
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getAllTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> courseTeacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseTeacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId,courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(courseTeacherLambdaQueryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher saveTeacher(AddCourseTeacherDto addCourseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();
        if(addCourseTeacherDto.getId()==null){
            BeanUtils.copyProperties(addCourseTeacherDto,courseTeacher);
            int insert = courseTeacherMapper.insert(courseTeacher);
            if(insert<=0){
                StudyOnlineException.cast("教师添加失败");
            }
            return courseTeacher;
        }else {
            CourseTeacher courseTeacher1 = courseTeacherMapper.selectById(addCourseTeacherDto.getId());
            BeanUtils.copyProperties(courseTeacher1,courseTeacher);
            courseTeacherMapper.updateById(courseTeacher);
        }
        return courseTeacher;
    }

    @Override
    public void deleteTeacher(Long courseId, Long id) {
        LambdaQueryWrapper<CourseTeacher> courseTeacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseTeacherLambdaQueryWrapper
                .eq(CourseTeacher::getId,id)
                .eq(CourseTeacher::getCourseId,courseId);
        CourseTeacher courseTeacher = courseTeacherMapper.selectOne(courseTeacherLambdaQueryWrapper);
        if(courseTeacher!=null){
            courseTeacherMapper.deleteById(courseTeacher);
        }
    }
}
