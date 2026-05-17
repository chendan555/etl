package com.gx.etl.datasource.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/equip/classify")
public class ClassifyController {

    @GetMapping("/tree")
    public Result<?> tree() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/info")
    public Result<?> info() {
        return Result.ok(new HashMap<>());
    }

    @DeleteMapping("/delete")
    public Result<?> delete() {
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/selectMaxId")
    public Result<?> selectMaxId() {
        return Result.ok(0);
    }
}
