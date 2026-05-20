package com.gx.etl.sample.controller;

import com.gx.etl.common.Result;
import com.gx.etl.sample.entity.SampleData;
import com.gx.etl.sample.entity.SamplePlan;
import com.gx.etl.sample.entity.SampleSubData;
import com.gx.etl.sample.service.SampleCascadeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/sample")
public class SampleCascadeController {

    @Resource
    private SampleCascadeService sampleCascadeService;

    /**
     * 获取所有样本方案（一级）
     */
    @GetMapping("/plans")
    public Result<List<SamplePlan>> getPlans() {
        return Result.ok(sampleCascadeService.getAllPlans());
    }

    /**
     * 根据方案ID获取样本数据（二级）
     */
    @GetMapping("/data")
    public Result<List<SampleData>> getDataByPlanId(@RequestParam Long planId) {
        return Result.ok(sampleCascadeService.getDataByPlanId(planId));
    }

    /**
     * 根据样本数据ID获取样本子数据（三级）
     */
    @GetMapping("/subData")
    public Result<List<SampleSubData>> getSubDataByDataId(@RequestParam Long dataId) {
        return Result.ok(sampleCascadeService.getSubDataByDataId(dataId));
    }
}
