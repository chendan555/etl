package com.gx.etl.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.datasource.entity.Category;
import com.gx.etl.datasource.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> getTree() {
        List<Category> all = list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        Map<Long, List<Category>> groupByParent = all.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));
        all.forEach(c -> c.setChildren(groupByParent.getOrDefault(c.getId(), new ArrayList<>())));
        return all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == -1)
                .collect(Collectors.toList());
    }
}
