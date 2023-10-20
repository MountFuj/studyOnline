package com.zy.content.service;

import com.zy.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
