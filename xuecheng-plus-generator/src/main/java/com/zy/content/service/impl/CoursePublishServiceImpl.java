package com.zy.content.service.impl;

import com.zy.content.model.po.CoursePublish;
import com.zy.content.mapper.CoursePublishMapper;
import com.zy.content.service.CoursePublishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author zhangyu
 */
@Slf4j
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {

}
