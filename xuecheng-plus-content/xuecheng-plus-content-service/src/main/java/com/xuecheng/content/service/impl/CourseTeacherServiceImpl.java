package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacher> queryCourseTeacherById(Long courseId) {
        QueryWrapper<CourseTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher saveCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        //查询出当前课程的机构id
        CourseBase courseBase = courseBaseMapper.selectById(courseTeacher.getCourseId());
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("不允许修改本机构外的课程");
        }
        //设置创建时间
        courseTeacher.setCreateDate(LocalDateTime.now());
        //根据是否存在id判断是新增还是修改
        if(courseTeacher.getId()==null){
            //新增，直接插入到数据库中
            //执行insert后会自动将id属性set到courseTeacher对象中
            int isSuccess = courseTeacherMapper.insert(courseTeacher);
            if(isSuccess<=0){
                XueChengPlusException.cast("添加教师失败");
            }
        }else {
            //修改
            int isSuccess = courseTeacherMapper.updateById(courseTeacher);
            if(isSuccess<=0){
                XueChengPlusException.cast("修改教师信息失败");
            }
        }
        //返回课程详细信息
        return courseTeacher;
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        QueryWrapper<CourseTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId).eq("id",teacherId);
        courseTeacherMapper.delete(queryWrapper);
    }
}
