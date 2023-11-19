package com.zy.content.service;

import com.zy.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {
     public CoursePreviewDto getCoursePreviewInfo(Long courseId);

     void commitAudit(Long companyId,Long courseId);
     void publishCourse(Long companyId,Long courseId);
}
