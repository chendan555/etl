package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @PostMapping("/rule/types")
    public Result<?> ruleTypes() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/rule/page")
    public Result<?> rulePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/rule/detail")
    public Result<?> ruleDetail(@RequestBody Map<String, Object> params) {
        return Result.ok(new HashMap<>());
    }

    @PostMapping("/rule/create")
    public Result<?> ruleCreate(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/rule/edit")
    public Result<?> ruleEdit(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/rule/audit")
    public Result<?> ruleAudit(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }
}
