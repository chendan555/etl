package com.gx.etl.flow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.flow.entity.PlanCategory;
import com.gx.etl.flow.entity.TransPlan;
import com.gx.etl.flow.service.PlanService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trans/plan")
public class PlanController {

    @Resource
    private PlanService planService;

    // ========== 方案分组树相关 ==========

    @GetMapping("/categroy/tree")
    public Result<?> getCategoryTree() {
        List<PlanCategory> tree = planService.getCategoryTree();
        return Result.ok(tree);
    }

    @GetMapping("/categroy/cache/tree")
    public Result<?> categoryCacheTree() {
        List<PlanCategory> tree = planService.getCategoryTree();
        return Result.ok(tree);
    }

    @PostMapping("/categroy/tree/save")
    public Result<?> saveCategoryTree(@RequestBody PlanCategory category) {
        // 前端可能传空字符串id，需转为null以触发新建逻辑
        if (category.getId() != null && category.getId() <= 0) {
            category.setId(null);
        }
        planService.saveCategory(category);
        return Result.ok();
    }

    @PostMapping("/categroy/tree/delete")
    public Result<?> deleteCategoryTree(@RequestParam("ids") Long id) {
        planService.deleteCategory(id);
        return Result.ok();
    }

    // ========== 方案 CRUD ==========

    @GetMapping("/page")
    public Result<?> getPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer flag,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String name) {

        Long groupIdLong = null;
        if (groupId != null && !groupId.isEmpty()) {
            try {
                groupIdLong = Long.parseLong(groupId);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        Page<TransPlan> page = planService.getPlanPage(pageIndex, pageSize, groupIdLong, name, flag);

        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @PostMapping("/savePlanAndDetail")
    public Result<?> savePlanAndDetail(@RequestBody TransPlan plan) {
        planService.savePlanAndDetail(plan);
        return Result.ok();
    }

    @PostMapping("/deletePlanAndDetail")
    public Result<?> deletePlanAndDetail(@RequestParam(required = false) Long id,
                                         @RequestBody(required = false) Map<String, Object> body) {
        Long planId = id;
        if (planId == null && body != null) {
            Object idObj = body.get("id");
            if (idObj instanceof Number) {
                planId = ((Number) idObj).longValue();
            } else if (idObj != null) {
                try {
                    planId = Long.parseLong(idObj.toString());
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        if (planId == null) {
            return Result.fail("参数不能为空");
        }
        planService.deletePlanAndDetail(planId);
        return Result.ok();
    }

    @PostMapping("/getTransPlanById")
    public Result<?> getTransPlanById(@RequestBody Map<String, Object> params) {
        Long planId = null;
        Object idObj = params.get("id");
        if (idObj instanceof Number) {
            planId = ((Number) idObj).longValue();
        } else if (idObj != null) {
            try {
                planId = Long.parseLong(idObj.toString());
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        if (planId == null) {
            return Result.ok(new HashMap<>());
        }
        TransPlan plan = planService.getPlanById(planId);
        return Result.ok(plan);
    }

    @GetMapping("/getTransByPlan")
    public Result<?> getTransByPlan(@RequestParam(required = false) Long planId) {
        if (planId == null) {
            return Result.ok(new HashMap<>());
        }
        TransPlan plan = planService.getPlanById(planId);
        return Result.ok(plan);
    }

    // ========== 方案执行 ==========

    @GetMapping("/start")
    public Result<?> startPlan(@RequestParam(required = false) Long id) {
        if (id == null) {
            return Result.fail("参数不能为空");
        }
        planService.startPlan(id);
        return Result.ok();
    }

    @GetMapping("/stop")
    public Result<?> stopPlan(@RequestParam(required = false) Long id) {
        if (id == null) {
            return Result.fail("参数不能为空");
        }
        planService.stopPlan(id);
        return Result.ok();
    }

    @GetMapping("/run/list")
    public Result<?> getRunList() {
        List<TransPlan> plans = planService.getRunList();
        return Result.ok(plans);
    }

    @GetMapping("/findTransList")
    public Result<?> findTransList(@RequestParam(required = false) Map<String, Object> params) {
        // 返回方案执行结果列表
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/yulan")
    public Result<?> yulan(@RequestParam(required = false) Map<String, Object> params) {
        // 返回方案执行结果预览
        return Result.ok(new ArrayList<>());
    }

    // ========== WebSocket ==========

    @GetMapping("/websocketUrl")
    public Result<?> getWebsocketUrl() {
        Map<String, Object> result = new HashMap<>();
        result.put("url", "ws://127.0.0.1:9056/websocket");
        return Result.ok(result);
    }

    // ========== 其他接口 ==========

    @PostMapping("/pageReorganizationByCondition")
    public Result<?> pageReorganization(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/update/reorganization")
    public Result<?> updateReorganization(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/page/data/query")
    public Result<?> dataQuery() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @GetMapping("/get/out/detail")
    public Result<?> outDetail() {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/getDataType")
    public Result<?> getDataType() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/getDataStatus")
    public Result<?> getDataStatus() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/getFileType")
    public Result<?> getFileType() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/getTransByPlan/list")
    public Result<?> getTransByPlanList() {
        return Result.ok(new ArrayList<>());
    }

    @PutMapping("/update/type")
    public Result<?> updateType(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/quit/type")
    public Result<?> quitType(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/create")
    public Result<?> createPlan() {
        return Result.ok();
    }

    @PostMapping("/edit")
    public Result<?> editPlan() {
        return Result.ok();
    }
}
