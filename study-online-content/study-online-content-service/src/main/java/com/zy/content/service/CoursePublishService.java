package com.zy.content.service;

import com.zy.content.model.dto.CoursePreviewDto;

import java.io.File;

public interface CoursePublishService {
     public CoursePreviewDto getCoursePreviewInfo(Long courseId);

     void commitAudit(Long companyId,Long courseId);
     void publishCourse(Long companyId,Long courseId);
     public File generateCourseHtml(Long courseId);
     public void  uploadCourseHtml(Long courseId,File file);
}
