package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @PostMapping("/analysis/context")
    public Result<?> analysisContext(@RequestBody Map<String, Object> params) {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/analysis/whereabouts")
    public Result<?> analysisWhereabouts() {
        return Result.ok(new HashMap<>());
    }
}
