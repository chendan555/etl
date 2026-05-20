package com.gx.etl.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.flow.entity.TaskGroup;
import com.gx.etl.flow.mapper.TaskGroupMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskGroupService extends ServiceImpl<TaskGroupMapper, TaskGroup> {

    /**
     * 获取全部任务组（树形结构）
     */
    public List<TaskGroup> getGroupTree() {
        List<TaskGroup> allGroups = list(new LambdaQueryWrapper<TaskGroup>()
                .orderByAsc(TaskGroup::getSortOrder));

        // 转换日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TaskGroup group : allGroups) {
            if (group.getCreateTime() != null) {
                group.setCreateDate(group.getCreateTime().format(formatter));
            }
            if (group.getUpdateTime() != null) {
                group.setUpdateDate(group.getUpdateTime().format(formatter));
            }
        }

        // 构建树形结构（检测循环引用）
        Set<Long> visited = new HashSet<>();
        visited.add(-1L);
        return buildTree(allGroups, -1L, visited);
    }

    /**
     * 构建树形结构
     */
    private List<TaskGroup> buildTree(List<TaskGroup> allGroups, Long parentId, Set<Long> visited) {
        return allGroups.stream()
                .filter(g -> parentId.equals(g.getParentId()))
                .filter(g -> visited.add(g.getIdTaskGroup()))
                .peek(g -> g.setChildren(buildTree(allGroups, g.getIdTaskGroup(), visited)))
                .collect(Collectors.toList());
    }

    /**
     * 保存任务组（新增或修改）
     */
    public void saveGroup(TaskGroup taskGroup) {
        if (taskGroup.getIdTaskGroup() != null && taskGroup.getIdTaskGroup() > 0) {
            // 修改
            taskGroup.setUpdateTime(LocalDateTime.now());
            updateById(taskGroup);
        } else {
            // 新增
            taskGroup.setCreateTime(LocalDateTime.now());
            taskGroup.setUpdateTime(LocalDateTime.now());
            taskGroup.setCountTrans(0);
            taskGroup.setSortOrder(0);
            if (taskGroup.getParentId() == null) {
                taskGroup.setParentId(-1L);
            }
            save(taskGroup);
        }
    }

    /**
     * 删除任务组
     */
    public void deleteGroups(List<Long> ids) {
        removeByIds(ids);
    }

    /**
     * 获取任务组详情
     */
    public TaskGroup getGroupDetail(Long idTaskGroup) {
        return getById(idTaskGroup);
    }

    /**
     * 获取全部任务组（平铺，创建前使用）
     */
    public List<TaskGroup> getAllGroups() {
        return list(new LambdaQueryWrapper<TaskGroup>()
                .orderByAsc(TaskGroup::getSortOrder));
    }
}
