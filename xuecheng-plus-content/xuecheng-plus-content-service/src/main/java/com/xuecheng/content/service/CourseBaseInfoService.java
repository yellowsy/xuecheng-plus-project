package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * 课程信息管理的接口
 */
public interface CourseBaseInfoService {
    /**
     * 分页查询
     * @param pageParams  分页查询参数
     * @param courseParamsDto 查询条件
     * @return  查询结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * 新增课程
     * @param companyId 机构id
     * @param addCourseDto 课程信息
     * @return  课程详细信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程
     * @param courseId 课程id
     * @return 课程详细信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * * 修改课程
     * @param editCourseDto  课程信息
     * @param companyId 机构id
     * @return
     */
    CourseBaseInfoDto modifyCourseBase(Long companyId,EditCourseDto editCourseDto);

    /**
     * 根据课程id删除对应的课程信息
     * @param courseId
     */
    void deleteCourseBase(Long companyId,Long courseId);
}
