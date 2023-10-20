package com.zy.content.service.impl;

import com.zy.content.model.po.CourseTeacher;
import com.zy.content.mapper.CourseTeacherMapper;
import com.zy.content.service.CourseTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author zhangyu
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

}
