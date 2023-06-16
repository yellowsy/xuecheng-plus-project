package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //树级菜单自连接
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //将list转为map便于存入子节点，并且排除根节点
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));//(key1,key2)->key2 两个key相同 以key2作为结果
        //循环遍历，处理父子节点关系
        //最终返回的list
        List<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();
        courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item->{
                    //1.存入父节点
                    if(id.equals(item.getParentid())){ //一级课程：该节点的父节点和根节点相等的
                        courseCategoryList.add(item);
                    }
                    //2.设置父节点的子节点
                    //2.1 首先获取该节点的父节点
                    CourseCategoryTreeDto parentTreeDto = mapTemp.get(item.getParentid());
                    if(parentTreeDto!=null){ //二级课程，因为已经过滤掉了根节点，所以一级课程没有父节点
                        if(parentTreeDto.getChildrenTreeNodes()==null){ //首次添加子节点，创建一个list
                            parentTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }
                        //后面的子节点直接放到父节点中即可
                        parentTreeDto.getChildrenTreeNodes().add(item);
                    }
                });
        return courseCategoryList;
    }
}
