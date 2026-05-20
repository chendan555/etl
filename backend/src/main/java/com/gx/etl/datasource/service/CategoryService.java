package com.gx.etl.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.datasource.entity.Category;
import com.gx.etl.datasource.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> getTree() {
        List<Category> all = list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        Map<Long, List<Category>> groupByParent = all.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));
        // 构建树时检测循环，防止 Jackson 序列化 StackOverflow
        Set<Long> visited = new HashSet<>();
        all.forEach(c -> c.setChildren(buildChildren(c.getId(), groupByParent, visited)));
        return all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == -1)
                .collect(Collectors.toList());
    }

    private List<Category> buildChildren(Long parentId, Map<Long, List<Category>> groupByParent, Set<Long> visited) {
        List<Category> children = groupByParent.getOrDefault(parentId, new ArrayList<>());
        List<Category> result = new ArrayList<>();
        for (Category child : children) {
            if (visited.contains(child.getId())) {
                continue; // 跳过已访问的节点，打断循环
            }
            visited.add(child.getId());
            child.setChildren(buildChildren(child.getId(), groupByParent, visited));
            result.add(child);
        }
        return result;
    }
}
