package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import com.gx.etl.receive.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj")
public class YjController {

    private final YjExecLogService yjExecLogService;
    private final YjHistoryService yjHistoryService;
    private final YjFileConfigService yjFileConfigService;
    private final YjTableConfigService yjTableConfigService;

    public YjController(YjExecLogService yjExecLogService,
                        YjHistoryService yjHistoryService,
                        YjFileConfigService yjFileConfigService,
                        YjTableConfigService yjTableConfigService) {
        this.yjExecLogService = yjExecLogService;
        this.yjHistoryService = yjHistoryService;
        this.yjFileConfigService = yjFileConfigService;
        this.yjTableConfigService = yjTableConfigService;
    }

    @GetMapping("/index/config/merge/num")
    public Result<?> indexMergeNum(@RequestParam(required = false, defaultValue = "1") Integer reqType) {
        long count;
        if (reqType == 1) {
            count = yjTableConfigService.count();
        } else {
            count = yjFileConfigService.count();
        }
        return Result.ok(count);
    }

    @GetMapping("/index/config/exec/situation")
    public Result<?> indexExecSituation() {
        Map<String, Object> r = new HashMap<>();
        long successCount = yjExecLogService.lambdaQuery()
                .eq(com.gx.etl.receive.entity.YjExecLog::getExecStatus, 1).count();
        long failCount = yjExecLogService.lambdaQuery()
                .eq(com.gx.etl.receive.entity.YjExecLog::getExecStatus, 0).count();
        r.put("success", successCount);
        r.put("fail", failCount);
        return Result.ok(r);
    }

    @GetMapping("/index/cumulative/volume")
    public Result<?> indexCumulativeVolume() {
        long total = yjHistoryService.count();
        return Result.ok(total);
    }
}
