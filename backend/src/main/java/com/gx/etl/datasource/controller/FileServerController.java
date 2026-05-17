package com.gx.etl.datasource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.datasource.entity.FileServer;
import com.gx.etl.datasource.service.FileServerService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/data/source/file")
public class FileServerController {

    private final FileServerService fileServerService;

    public FileServerController(FileServerService fileServerService) {
        this.fileServerService = fileServerService;
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int pageIndex,
                          @RequestParam(defaultValue = "10") int pageSize) {
        Page<FileServer> page = fileServerService.listPage(pageIndex, pageSize);
        return Result.ok(page.getRecords());
    }

    @PostMapping("/save/or/edit")
    public Result<?> save(@RequestBody FileServer fileServer) {
        fileServerService.saveOrUpdate(fileServer);
        return Result.ok(fileServer);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        fileServerService.removeById(id);
        return Result.ok();
    }
}
