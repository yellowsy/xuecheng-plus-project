package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        //1.拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //1.1 根据名称模糊查询 在sql中拼接 course_base.name like '%值%'
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()),CourseBase::getName,courseParamsDto.getCourseName());
        //1.2 根据审核状态查询 在sql中拼接 course_base.audit_status = ?
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,courseParamsDto.getAuditStatus());
        //1.3 根据课程发布状态查询   course_base.status = ?
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()),CourseBase::getStatus,courseParamsDto.getPublishStatus());
        //2.创建page分页参数对象 当前页码：1 每页记录数：2
        //封装分页参数对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //4.封装对象返回
        //4.1 查询结果中获取数据列表
        List<CourseBase> items = pageResult.getRecords();
        //4.2 总记录数
        long total = pageResult.getTotal();
        PageResult<CourseBase> result = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        return result;
    }

    @Transactional //涉及增删改加上该注解
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto dto) {
        //1.参数合法性校验 (也可以使用JRS303校验框架,一般用于表单校验)
        /*if (StringUtils.isBlank(dto.getName())) {
            throw new XueChengPlusException("课程名称为空");
        }
        if (StringUtils.isBlank(dto.getMt())) {
            throw new XueChengPlusException("课程分类为空");
        }
        if (StringUtils.isBlank(dto.getSt())) {
            throw new XueChengPlusException("课程分类为空");
        }
        if (StringUtils.isBlank(dto.getGrade())) {
            throw new XueChengPlusException("课程等级为空");
        }
        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new XueChengPlusException("教育模式为空");
        }
        if (StringUtils.isBlank(dto.getUsers())) {
            throw new XueChengPlusException("适应人群");
        }
        if (StringUtils.isBlank(dto.getCharge())) {
            throw new XueChengPlusException("收费规则为空");
        }*/
        //2.向课程基本信息course_base写入数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(dto,courseBase); //将dto中的数据拷贝到courseBase,只要属性名称一直就可以拷贝
        //设置审核状态：未发布
        courseBase.setAuditStatus("202002");
        //设置发布状态：未发布
        courseBase.setStatus("203001");
        //设置机构id
        courseBase.setCompanyId(companyId);
        //添加时间
        courseBase.setCreateDate(LocalDateTime.now());
        //插入到数据库中
        int isSuccess = courseBaseMapper.insert(courseBase);
        if(isSuccess<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }
        //3.向课程营销信息course_market写入数据  (与课程信息一一对应，主键相同)
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        int count = saveCourseMarket(courseMarket);
        if(count<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }
        //4.根据课程id返回课程详细信息
        return getCourseBaseInfo(courseId);
    }

    //保存营销信息，逻辑：存在就更新，不存在就添加
    private int saveCourseMarket(CourseMarket courseMarket){
        //1.数据校验
        //收费规则
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice().floatValue()<=0){
                throw new XueChengPlusException("课程为收费价格不能为空且必须大于0");
            }
        }
        //2.根据id从课程营销表查询;
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId());
        if(courseMarketObj==null){  //新增
            return courseMarketMapper.insert(courseMarket);
        }else { //修改
            BeanUtils.copyProperties(courseMarket,courseMarketObj);
            courseMarketObj.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //返回课程详细信息
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //TODO 查询分类名称
        CourseCategory mtObj = courseCategoryMapper.selectById(courseBase.getMt());
        String mtName = mtObj.getName();//大分类名称
        courseBaseInfoDto.setMtName(mtName);
        CourseCategory stObj = courseCategoryMapper.selectById(courseBase.getSt());
        String stName = stObj.getName();//小分类名称
        courseBaseInfoDto.setStName(stName);
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto modifyCourseBase(Long companyId,EditCourseDto editCourseDto) {
        //1.查询课程
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            XueChengPlusException.cast("课程不存在");
        }
        //2.数据校验 本机构只能修改本机构的课程
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        //3.封装数据
        //3.1 更新课程信息
        BeanUtils.copyProperties(editCourseDto,courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //3.2 更新营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        courseMarket.setId(courseId);//防止editCourseDto营销id为空，将原来的id覆盖

        int count = courseBaseMapper.updateById(courseBase);
        if(count<=0){
            XueChengPlusException.cast("修改课程失败");
        }
        courseMarketMapper.updateById(courseMarket);

        return getCourseBaseInfo(courseId);
    }

    @Override
    public void deleteCourseBase(Long companyId,Long courseId) {
        //只能删除本机构的课程
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("只能删除本机构的课程");
        }
        //1.根据课程id删除课程计划
        QueryWrapper<Teachplan> teachplanQueryWrapper = new QueryWrapper<>();
        teachplanQueryWrapper.eq("course_id",courseId);
        teachplanMapper.delete(teachplanQueryWrapper);
        //2.根据课程id删除课程媒资信息
        QueryWrapper<TeachplanMedia> teachplanMediaQueryWrapper = new QueryWrapper<>();
        teachplanMediaQueryWrapper.eq("course_id",courseId);
        teachplanMediaMapper.delete(teachplanMediaQueryWrapper);
        //3.删除课程对应的教师信息
        QueryWrapper<CourseTeacher> courseTeacherQueryWrapper = new QueryWrapper<>();
        courseTeacherQueryWrapper.eq("course_id",courseId);
        courseTeacherMapper.delete(courseTeacherQueryWrapper);
        //4.删除营销信息
        courseMarketMapper.deleteById(courseId);
        //5.删除课程信息
        courseBaseMapper.deleteById(courseId);
    }
}
