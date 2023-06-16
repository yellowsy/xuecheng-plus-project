package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    /**
     * 根据课程id查询教师
     * @param courseId 课程id
     * @return
     */
    List<CourseTeacher> queryCourseTeacherById(Long courseId);

    /**
     * 新增/修改教师
     * @return 返回教师详细信息
     */
    CourseTeacher saveCourseTeacher(Long companyId, CourseTeacher courseTeacher);

    /**
     * 根据课程id和教师id删除课程信息
     * @param courseId 课程id
     * @param teacherId 教师id (主键)
     */
    void deleteCourseTeacher(Long courseId, Long teacherId);
}
