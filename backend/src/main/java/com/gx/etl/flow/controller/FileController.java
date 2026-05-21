package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import com.gx.etl.receive.service.YjExecService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private final YjExecService yjExecService;

    public FileController(YjExecService yjExecService) {
        this.yjExecService = yjExecService;
    }

    @PostMapping("/analysis/context.do")
    public Result<?> analysisContext(@RequestBody Map<String, Object> params) {
        try {
            Long configId = params.get("configId") != null ? Long.parseLong(params.get("configId").toString()) : null;
            if (configId == null) {
                return Result.fail("configId不能为空");
            }
            String batch = params.getOrDefault("batch", yjExecService.generateBatchNumber()).toString();
            String createBy = params.getOrDefault("createBy", "admin").toString();
            yjExecService.execFile(configId, batch, createBy);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/analysis/whereabouts.do")
    public Result<?> analysisWhereabouts() {
        return Result.ok(new HashMap<>());
    }
}
