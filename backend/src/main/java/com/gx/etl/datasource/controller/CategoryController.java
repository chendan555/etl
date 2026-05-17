package com.gx.etl.datasource.controller;

import com.gx.etl.common.Result;
import com.gx.etl.datasource.entity.Category;
import com.gx.etl.datasource.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/center/database/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public Result<?> tree() {
        List<Category> tree = categoryService.getTree();
        return Result.ok(tree);
    }

    @GetMapping("/list")
    public Result<?> list() {
        List<Category> list = categoryService.list();
        return Result.ok(list);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Category category) {
        categoryService.saveOrUpdate(category);
        return Result.ok(category);
    }

    @PostMapping("/del")
    public Result<?> delete(@RequestParam Long id) {
        categoryService.removeById(id);
        return Result.ok();
    }
}
