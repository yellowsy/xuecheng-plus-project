package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程教师相关信息
 */
@Api(value = "课程教师编辑接口",tags = "课程教师编辑接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("根据id查询教师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> queryCourseTeacherById(@PathVariable Long courseId){
        List<CourseTeacher> courseTeachers = courseTeacherService.queryCourseTeacherById(courseId);
        return courseTeachers;
    }

    @ApiOperation("添加/修改教师信息接口")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody CourseTeacher courseTeacher){
        Long companyId=1232141425L;
        return courseTeacherService.saveCourseTeacher(companyId,courseTeacher);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId,@PathVariable Long teacherId){
        courseTeacherService.deleteCourseTeacher(courseId,teacherId);
    }
}
