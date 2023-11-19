package com.zy.content.api;

import com.zy.content.model.dto.CoursePreviewDto;
import com.zy.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/11/13 19:33
 */
@Controller
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        ModelAndView coursepreview = new ModelAndView();
        coursepreview.addObject("model",coursePreviewInfo);
        coursepreview.setViewName("course_template");
        return coursepreview;
    }
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1L;
        coursePublishService.commitAudit(companyId,courseId);
    }
    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){

    }
}
