package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/es")
public class EsController {

    @PostMapping("/imitate/call.do")
    public Result<?> imitateCall(@RequestBody Map<String, Object> params) {
        // 仿真引接 - 暂时返回成功，后续实现
        return Result.ok();
    }
}
