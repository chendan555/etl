package com.gx.etl.receive.controller;

import com.gx.etl.common.Result;
import com.gx.etl.receive.service.YjExecService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/batch")
public class YjBatchController {

    private final YjExecService yjExecService;

    public YjBatchController(YjExecService yjExecService) {
        this.yjExecService = yjExecService;
    }

    @GetMapping("/num/generate")
    public Result<?> generateBatchNum() {
        return Result.ok(yjExecService.generateBatchNumber());
    }

    @GetMapping("/num/list")
    public Result<?> getBatchNumList() {
        List<Map<String, Object>> list = new ArrayList<>();
        return Result.ok(list);
    }
}
