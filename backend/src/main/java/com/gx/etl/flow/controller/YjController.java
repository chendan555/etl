package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj")
public class YjController {

    @PostMapping("/exec/table/page")
    public Result<?> execTablePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/exec/file/page")
    public Result<?> execFilePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/exec/table/exec")
    public Result<?> execTable(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/record/simulation/page")
    public Result<?> simulationPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/record/history/page")
    public Result<?> historyPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @GetMapping("/record/history/del")
    public Result<?> historyDel() {
        return Result.ok();
    }

    @PostMapping("/record/check/page")
    public Result<?> checkPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/jsonData/saveJsonConfig")
    public Result<?> saveJsonConfig(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/jsonData/queryJsonConfigById")
    public Result<?> queryJsonConfigById() {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/batch/num/generate")
    public Result<?> batchNumGenerate() {
        return Result.ok("1");
    }

    @PostMapping("/protocol/page")
    public Result<?> protocolPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/protocol/save")
    public Result<?> protocolSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @DeleteMapping("/protocol/delete")
    public Result<?> protocolDelete() {
        return Result.ok();
    }

    @PostMapping("/form/page")
    public Result<?> formPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/form/save")
    public Result<?> formSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @DeleteMapping("/form/delete")
    public Result<?> formDelete() {
        return Result.ok();
    }

    @PostMapping("/tableConfig/queryListTableConfig")
    public Result<?> queryListTableConfig() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/tableConfig/saveTableConfigAndDetail")
    public Result<?> saveTableConfigAndDetail(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @DeleteMapping("/tableConfig/deleteTableConfigAndDetail")
    public Result<?> deleteTableConfigAndDetail() {
        return Result.ok();
    }

    @PostMapping("/tableConfig/getTableConfigList")
    public Result<?> getTableConfigList() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/tableConfig/queryTableConfigById")
    public Result<?> queryTableConfigById() {
        return Result.ok(new HashMap<>());
    }

    @PostMapping("/config/table/page")
    public Result<?> configTablePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/config/table/delete")
    public Result<?> configTableDelete(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/config/table/save")
    public Result<?> configTableSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/config/file/page")
    public Result<?> configFilePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/config/file/save")
    public Result<?> configFileSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/config/file/delete")
    public Result<?> configFileDelete(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/config/simulation/page")
    public Result<?> configSimulationPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/config/simulation/save")
    public Result<?> configSimulationSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/config/simulation/delete")
    public Result<?> configSimulationDelete(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/protocol/file/merge")
    public Result<?> protocolFileMerge(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/index/config/merge/num")
    public Result<?> indexMergeNum() {
        return Result.ok(0);
    }

    @GetMapping("/index/config/exec/situation")
    public Result<?> indexExecSituation() {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/index/cumulative/volume")
    public Result<?> indexCumulativeVolume() {
        return Result.ok(0);
    }
}
