package com.gx.etl.datasource.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 数据源相关接口（仿真数据、文件消息等）
 */
@RestController
@RequestMapping("/api/data/source")
public class DataSourceController {

    /**
     * 获取仿真数据列表
     */
    @GetMapping("/artificial/list")
    public Result<?> getArtificialList(@RequestParam(required = false) String sourceId) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 保存或编辑仿真数据
     */
    @PostMapping("/artificial/save/or/edit")
    public Result<?> saveArtificial(@RequestBody Map<String, Object> params) {
        return Result.ok(params);
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.ok();
    }

    /**
     * 获取文件消息列表
     */
    @GetMapping("/fileMessage/list/all")
    public Result<?> getFileMessageList(@RequestParam(required = false) Long id) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 文件下载
     */
    @GetMapping("/fileMessage/download")
    public Result<?> downloadFile(@RequestParam(required = false) String path) {
        return Result.ok();
    }

    /**
     * FTP文件上传
     */
    @PostMapping("/fileMessage/ftp/upload")
    public Result<?> ftpUpload(@RequestBody(required = false) Map<String, Object> params) {
        return Result.ok();
    }

    /**
     * FTP文件记录列表
     */
    @PostMapping("/fileMessage/ftp/record/list")
    public Result<?> ftpRecordList(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", new ArrayList<>());
        result.put("total", 0);
        return Result.ok(result);
    }
}
