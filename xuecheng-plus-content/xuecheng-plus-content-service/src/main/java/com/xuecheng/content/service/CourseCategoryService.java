package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;


public interface CourseCategoryService {
    /**
     * 课程分类树形结构查询
     * @param id  根节点
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
