package com.zy.content.service;

import com.zy.content.model.dto.BindTeachplanMediaDto;
import com.zy.content.model.dto.SaveTeachplanDto;
import com.zy.content.model.dto.TeachplanDto;
import com.zy.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {
    public List<TeachplanDto> findTeachplayTree(long courseId);
    public void saveTeachplan(SaveTeachplanDto teachplanDto);
    public void deleteTeachplan(Long courseId);
   public void orderByTeachPlan(String moveType,Long teachplanId);
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
