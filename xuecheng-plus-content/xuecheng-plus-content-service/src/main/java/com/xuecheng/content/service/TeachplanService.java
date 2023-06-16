package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * 课程计划相关接口
 */
public interface TeachplanService {
    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增/修改课程计划信息
     * @param saveTeachplanDto
     */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId
     */
    void deleteTeachplan(Long teachplanId);

    /**
     * 上移课程计划
     * @param teachplanId 课程id
     */
    void moveupTeachplan(Long teachplanId);

    /**
     * 下移课程计划
     * @param teachplanId 课程id
     */
    void movedownTeachplan(Long teachplanId);

    /**
     * 教学计划绑定媒资信息
     * @param bindTeachplanMediaDto
     */
    void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
