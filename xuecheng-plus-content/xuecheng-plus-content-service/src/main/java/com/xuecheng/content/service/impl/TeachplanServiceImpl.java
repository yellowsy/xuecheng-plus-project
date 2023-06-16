package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    private int getCountTeachplan(Long courseId,Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count+1;
    }
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //根据返回数据中是否包含课程id来判断是新增还是修改
        Long id = saveTeachplanDto.getId();
        if(id==null){
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定排序字段，找到它的同级节点个数，排序字段就是个数加1  select count(1) from teachplan where course_id=117 and parentid=268
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentid = saveTeachplanDto.getParentid();
            int countTeachplan = getCountTeachplan(courseId, parentid);
            teachplan.setOrderby(countTeachplan);
            teachplanMapper.insert(teachplan);
        }
        else {
            //修改
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long teachplanId) {
        //如果是大章节，不能直接删除
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan.getParentid()==0){
            //根据大章节id查询出所有小节
            List<TeachplanDto> teachplanDtos = teachplanMapper.selectByParentId(teachplanId);
            if(teachplanDtos.size()!=0){
                XueChengPlusException.cast("课程计划信息还有小节内容，无法操作");
            }
            //下面没有小节，删除该大章节
            teachplanMapper.deleteById(teachplanId);
        }else {
            //小节直接删除
            teachplanMapper.deleteById(teachplanId);
            //删除对应的媒资信息
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper = queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
            teachplanMediaMapper.delete(queryWrapper);
        }
    }

    @Override
    public void moveupTeachplan(Long teachplanId) {
        //查询出课程计划
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //将orderby属性减一，如果是一，则为最上层
        Integer orderby = teachplan.getOrderby();
        if(orderby<=1){
            XueChengPlusException.cast("已经在最上层，不能上移");
        }
        //找出它的上一条数据
        QueryWrapper<Teachplan> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper
                .eq("parentid", teachplan.getParentid())
                .eq("orderby", orderby - 1)
                .eq("course_id",teachplan.getCourseId());
        Teachplan pervTeachplan = teachplanMapper.selectOne(queryWrapper);

        teachplan.setOrderby(orderby-1);
        pervTeachplan.setOrderby(orderby);
        //修改数据库
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(pervTeachplan);
    }

    @Override
    public void movedownTeachplan(Long teachplanId) {
        //查询出课程计划
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //将orderby属性加一，如果是最后一条数据则不能下移
        //找到它的同级节点个数
        int count = getCountTeachplan(teachplan.getCourseId(), teachplan.getParentid())-1;
        Integer orderby = teachplan.getOrderby();
        if(orderby>=count){
            XueChengPlusException.cast("最后一条数据则,不能下移");
        }
        //查询出后一条数据
        QueryWrapper<Teachplan> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper
                .eq("parentid", teachplan.getParentid())
                .eq("orderby", orderby + 1)
                .eq("course_id",teachplan.getCourseId());
        Teachplan nextTeachplan = teachplanMapper.selectOne(queryWrapper);

        teachplan.setOrderby(orderby+1);
        nextTeachplan.setOrderby(orderby);
        //修改数据库
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(nextTeachplan);
    }

    @Transactional
    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //课程计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            throw new XueChengPlusException("课程计划不存在");
        }
        //先删除原有记录,根据课程计划id删除它所绑定的媒资
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));
        //再添加新记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
    }
}
