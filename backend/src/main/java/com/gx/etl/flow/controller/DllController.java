package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dll")
public class DllController {

    @PostMapping("/exec/model/save")
    public Result<?> modelSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/exec/list/page")
    public Result<?> listPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/exec/data/file/upload")
    public Result<?> fileUpload() {
        return Result.ok();
    }

    @GetMapping("/exec/model/del")
    public Result<?> modelDel() {
        return Result.ok();
    }

    @PostMapping("/exec/model/exec")
    public Result<?> modelExec(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }
}
