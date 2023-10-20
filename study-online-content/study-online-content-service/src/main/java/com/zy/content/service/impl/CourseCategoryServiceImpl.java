package com.zy.content.service.impl;

import com.zy.content.mapper.CourseCategoryMapper;
import com.zy.content.model.dto.CourseCategoryTreeDto;
import com.zy.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description 查询课程菜单分类
 * @date 2023/10/19 18:24
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //查询数据库得到的课程分类
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //最终返回的列表
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        HashMap<String, CourseCategoryTreeDto> mapTemp = new HashMap<>();

        courseCategoryTreeDtos.stream().forEach(item->{
            mapTemp.put(item.getId(),item);
            //只将根节点的下级节点放入list
            if(item.getParentid().equals(id)){
                categoryTreeDtos.add(item);
            }
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes() ==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //向节点的下级节点list加入节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
}
