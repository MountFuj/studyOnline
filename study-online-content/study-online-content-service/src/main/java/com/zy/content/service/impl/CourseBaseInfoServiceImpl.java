package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.base.exception.CommonError;
import com.zy.base.exception.RestErrorResponse;
import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.content.mapper.CourseBaseMapper;
import com.zy.content.mapper.CourseCategoryMapper;
import com.zy.content.mapper.CourseMarketMapper;
import com.zy.content.model.dto.AddCourseDto;
import com.zy.content.model.dto.CourseBaseInfoDto;
import com.zy.content.model.dto.EditCourseDto;
import com.zy.content.model.dto.QueryCourseParamsDto;
import com.zy.content.model.po.CourseBase;
import com.zy.content.model.po.CourseCategory;
import com.zy.content.model.po.CourseMarket;
import com.zy.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/17 13:47
 */
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //创建查询条件
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        //课程名称模糊匹配
        wrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        //审核状态
        wrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //发布状态
        wrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        //page对象
        Page<CourseBase> courseBasePage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> basePage = courseBaseMapper.selectPage(courseBasePage, wrapper);
        //数据
        List<CourseBase> items = basePage.getRecords();
        //总条数
        long total = basePage.getTotal();
        //封装数据
        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(items,total,pageParams.getPageNo(),pageParams.getPageSize());
        //返回结果
        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
//        //合法性校验
//        if (StringUtils.isBlank(dto.getName())) {
////            throw new RuntimeException("课程名称为空");
//            StudyOnlineException.cast("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(dto.getMt())) {
//            throw new RuntimeException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            throw new RuntimeException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
//        }
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        Long courseId = courseBaseNew.getId();
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        //收费规则
        String charge = dto.getCharge();

        //收费课程必须写价格且价格大于0
        if(charge.equals("201001")){
            Float price = dto.getPrice();
            if(price == null || price.floatValue()<=0){
                throw new RuntimeException("课程设置了收费价格不能为空且必须大于0");
            }
        }

        //插入课程营销信息
        int insert1 = courseMarketMapper.insert(courseMarketNew);

        if(insert<=0 || insert1<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }
        //添加成功
        //返回添加的课程信息
        return getCourseBaseInfo(courseId);

    }
    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        if (courseBase == null) {
            return null;
        }
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        //业务规则校验，本机构只允许修改本机构的课程
        //课程id
        Long courseId = dto.getId();
        CourseBase courseBase_u = courseBaseMapper.selectById(courseId);
        if(courseBase_u==null){
            StudyOnlineException.cast("课程信息不存在");
        }
        if(!companyId.equals(courseBase_u.getCompanyId())){
            StudyOnlineException.cast("本机构只允许修改本机构的课程");
        }

        //封装数据
        //将请求参数拷贝到待修改对象中
        BeanUtils.copyProperties(dto,courseBase_u);
        courseBase_u.setChangeDate(LocalDateTime.now());
        //更新到数据库
        int i = courseBaseMapper.updateById(courseBase_u);

        //查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if(courseMarket==null){
            courseMarket = new CourseMarket();
        }

        //判断是否收费
        String charge = dto.getCharge();
        if(charge.equals("201001")){
            Float price = dto.getPrice();
            if(price == null || price.floatValue()<=0){
                StudyOnlineException.cast("课程设置了收费价格不能为空且必须大于0");
            }
        }

        //将dto中的课程营销信息拷贝至courseMarket对象中
        BeanUtils.copyProperties(dto,courseMarket);

        int i1 = courseMarketMapper.updateById(courseMarket);
        if(i1<=0){
            StudyOnlineException.cast("修改课程失败");
        }

        return getCourseBaseInfo(courseId);
    }
}
