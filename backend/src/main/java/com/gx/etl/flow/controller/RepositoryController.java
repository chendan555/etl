package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.service.TransformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/repository")
public class RepositoryController {

    @Resource
    private TransformationService transformationService;

    @PostMapping("/new/createTrans")
    public Result<?> createTrans(@RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        Long taskGroupId = null;
        Object groupIdObj = params.get("taskGroupId");
        if (groupIdObj != null) {
            if (groupIdObj instanceof Number) {
                taskGroupId = ((Number) groupIdObj).longValue();
            } else {
                try {
                    taskGroupId = Long.parseLong(groupIdObj.toString());
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        String describe = (String) params.get("describe");
        String bindTableNames = (String) params.get("bindTableNames");

        Long transId = transformationService.saveTransformation(name, taskGroupId, null, null, describe, bindTableNames);
        return Result.ok(transId);
    }

    @PostMapping("/new/updateTrans")
    public Result<?> updateTrans(@RequestBody Map<String, Object> params) {
        Long id = null;
        Object idObj = params.get("id");
        if (idObj instanceof Number) {
            id = ((Number) idObj).longValue();
        } else if (idObj != null) {
            try {
                id = Long.parseLong(idObj.toString());
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        String name = (String) params.get("name");
        String describe = (String) params.get("describe");
        String bindTableNames = (String) params.get("bindTableNames");

        if (id != null) {
            transformationService.updateTransformation(id, name, null, null, describe, bindTableNames);
        }
        return Result.ok();
    }

    @PostMapping("/new/createYJTrans")
    public Result<?> createYJTrans(@RequestBody Map<String, Object> params) {
        // 创建引接转换
        String name = (String) params.get("name");
        Long transId = transformationService.saveTransformation(name, null, null, null, null);
        return Result.ok(transId);
    }

    @PostMapping("/new/updateYJTrans")
    public Result<?> updateYJTrans(@RequestBody Map<String, Object> params) {
        Long id = null;
        Object idObj = params.get("id");
        if (idObj instanceof Number) {
            id = ((Number) idObj).longValue();
        } else if (idObj != null) {
            try {
                id = Long.parseLong(idObj.toString());
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        String name = (String) params.get("name");

        if (id != null) {
            transformationService.updateTransformation(id, name, null, null, null);
        }
        return Result.ok();
    }

    @PostMapping("/createTrans")
    public Result<?> createTransLegacy(@RequestBody Map<String, Object> params) {
        return createTrans(params);
    }

    @PutMapping("/new/updateTransToModel")
    public Result<?> updateTransToModel(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/new/addCopyTrans")
    public Result<?> addCopyTrans(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }
}
