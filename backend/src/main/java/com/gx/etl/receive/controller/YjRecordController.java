package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjExecLog;
import com.gx.etl.receive.entity.YjHistory;
import com.gx.etl.receive.entity.YjJsonConfig;
import com.gx.etl.receive.service.YjExecLogService;
import com.gx.etl.receive.service.YjHistoryService;
import com.gx.etl.receive.service.YjJsonConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/record")
public class YjRecordController {

    private final YjHistoryService yjHistoryService;
    private final YjJsonConfigService yjJsonConfigService;
    private final YjExecLogService yjExecLogService;

    public YjRecordController(YjHistoryService yjHistoryService,
                              YjJsonConfigService yjJsonConfigService,
                              YjExecLogService yjExecLogService) {
        this.yjHistoryService = yjHistoryService;
        this.yjJsonConfigService = yjJsonConfigService;
        this.yjExecLogService = yjExecLogService;
    }

    /**
     * 引接记录分页查询
     */
    @PostMapping("/history/page")
    public Result<?> historyPage(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        String mainBody = params.get("mainBody") != null ? params.get("mainBody").toString() : null;
        if (mainBody != null && mainBody.isEmpty()) mainBody = null;

        // 支持前端 searchData 中的字段
        String batchNum = params.get("batchNum") != null ? params.get("batchNum").toString() : null;
        if (batchNum != null && batchNum.isEmpty()) batchNum = null;

        String toTable = params.get("tableName") != null ? params.get("tableName").toString() : null;
        if (toTable != null && toTable.isEmpty()) toTable = null;

        Long configId = null;
        Object yjMappingId = params.get("yjMappingId");
        if (yjMappingId != null) {
            try { configId = Long.parseLong(yjMappingId.toString()); } catch (Exception e) {}
        }

        Page<YjHistory> page = yjHistoryService.listPage(pageIndex, pageSize, mainBody, batchNum, configId, toTable);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    /**
     * 删除引接记录
     */
    @GetMapping("/history/del")
    public Result<?> historyDel(@RequestParam Long id) {
        yjHistoryService.removeById(id);
        return Result.ok();
    }

    /**
     * 查看引接记录详情（执行日志）
     */
    @PostMapping("/check/page")
    public Result<?> checkPage(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        // 根据history的configId和configType查询执行日志
        String configType = params.get("configType") != null ? params.get("configType").toString() : null;
        Long configId = null;
        if (params.get("configId") != null) {
            try { configId = Long.parseLong(params.get("configId").toString()); } catch (Exception e) {}
        }

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<YjExecLog> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (configId != null) {
            wrapper.eq(YjExecLog::getConfigId, configId);
        }
        if (configType != null && !configType.isEmpty()) {
            wrapper.eq(YjExecLog::getConfigType, configType);
        }
        wrapper.orderByDesc(YjExecLog::getCreateTime);

        Page<YjExecLog> page = new Page<>(pageIndex, pageSize);
        page = yjExecLogService.page(page, wrapper);

        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    /**
     * 仿真记录分页查询
     */
    @PostMapping("/simulation/page")
    public Result<?> simulationPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("records", new java.util.ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
}
