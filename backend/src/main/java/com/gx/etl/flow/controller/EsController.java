package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/es")
public class EsController {

    @PostMapping("/imitate/call")
    public Result<?> imitateCall(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }
}
