package com.zy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.base.exception.StudyOnlineException;
import com.zy.content.mapper.TeachplanMapper;
import com.zy.content.mapper.TeachplanMediaMapper;
import com.zy.content.model.dto.SaveTeachplanDto;
import com.zy.content.model.dto.TeachplanDto;
import com.zy.content.model.po.Teachplan;
import com.zy.content.model.po.TeachplanMedia;
import com.zy.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/21 8:55
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> findTeachplayTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);
            teachplanMapper.insert(teachplanNew);
        }
    }
    /***
     * @description 删除课程计划
     * @param courseId 课程id
     * @return
     * @author
     * @date
    */

    @Override
    public void deleteTeachplan(Long courseId) {
        //前提是为提交的课程才可以删除
        //1.查出对应id的课程
        Teachplan teachplan = teachplanMapper.selectById(courseId);
        if(teachplan.getStatus()!=202003){
            //2.判断是否为章节
            if(teachplan.getGrade()==1){
                //2.1判断章节下是否有小节
                LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Teachplan::getParentid,courseId);
                List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
                if(teachplans.size()!=0){
                    StudyOnlineException.cast("课程计划信息还有子级信息，无法操作");
                }
                teachplanMapper.deleteById(teachplan);
            }
            //3.判断是否为小节
            if(teachplan.getGrade()==2){
                //3.1删除小节关联的视频信息
                Long id = teachplan.getId();
                LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
                teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId,id);
                TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(teachplanMediaLambdaQueryWrapper);
                if(teachplanMedia!=null){
                    teachplanMediaMapper.deleteById(teachplanMedia);
                }
                teachplanMapper.deleteById(teachplan);
            }
        }
    }

    @Override
    public void orderByTeachPlan(String moveType, Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 获取层级和当前orderby，章节移动和小节移动的处理方式不同
        Integer grade = teachplan.getGrade();
        Integer orderby = teachplan.getOrderby();
        // 章节移动是比较同一课程id下的orderby
        Long courseId = teachplan.getCourseId();
        // 小节移动是比较同一章节id下的orderby
        Long parentid = teachplan.getParentid();
        if ("moveup".equals(moveType)) {
            if (grade == 1) {
                // 章节上移，找到上一个章节的orderby，然后与其交换orderby
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1  AND orderby < 1 ORDER BY orderby DESC limit 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getGrade, 1)
                        .eq(Teachplan::getCourseId, courseId)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderBy(teachplan, tmp);
            } else if (grade == 2) {
                // 小节上移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby < 5 ORDER BY orderby DESC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderBy(teachplan, tmp);
            }

        } else if ("movedown".equals(moveType)) {
            if (grade == 1) {
                // 章节下移
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getGrade, grade)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderBy(teachplan, tmp);
            } else if (grade == 2) {
                // 小节下移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderBy(teachplan, tmp);
            }
        }
    }

    private void exchangeOrderBy(Teachplan teachplan, Teachplan teachplanDown) {
        if(teachplanDown==null){
            StudyOnlineException.cast("不能够移动了哦");
        }
        Integer orderby = teachplan.getOrderby();
        Integer orderby1 = teachplanDown.getOrderby();
        teachplan.setOrderby(orderby1);
        teachplanDown.setOrderby(orderby);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanDown);
    }
}
