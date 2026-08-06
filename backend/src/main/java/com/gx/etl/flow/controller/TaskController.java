package com.gx.etl.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.flow.entity.TaskGroup;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.service.TaskGroupService;
import com.gx.etl.flow.service.TransformationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Resource
    private TaskGroupService taskGroupService;

    @Resource
    private TransformationService transformationService;

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    // ========== 项目分组相关接口 ==========

    @GetMapping("/group/all/list")
    public Result<?> getGroupAllList() {
        List<TaskGroup> tree = taskGroupService.getGroupTree();
        return Result.ok(tree);
    }

    @PostMapping("/group/getGroupList")
    public Result<?> getGroupList() {
        List<TaskGroup> tree = taskGroupService.getGroupTree();
        return Result.ok(tree);
    }

    @PostMapping("/group/getAllTaskGroupBeforeCreate")
    public Result<?> getAllTaskGroupBeforeCreate() {
        List<TaskGroup> groups = taskGroupService.getAllGroups();
        return Result.ok(groups);
    }

    @PostMapping("/group/save")
    public Result<?> saveGroup(@RequestBody TaskGroup taskGroup) {
        taskGroupService.saveGroup(taskGroup);
        return Result.ok();
    }

    @GetMapping("/group/detail")
    public Result<?> getGroupDetail(@RequestParam(required = false) Long taskGroupCode,
                                    @RequestParam(required = false) Long idTaskGroup) {
        Long id = idTaskGroup != null ? idTaskGroup : taskGroupCode;
        if (id == null) {
            return Result.fail("参数不能为空");
        }
        TaskGroup group = taskGroupService.getGroupDetail(id);
        return Result.ok(group);
    }

    @GetMapping("/group/datasource/info")
    public Result<?> getGroupDatasourceInfo(@RequestParam(required = false) Long taskGroupCode) {
        // 返回数据源信息（暂时返回空）
        return Result.ok(new HashMap<>());
    }

    @PostMapping("/group/create")
    public Result<?> createGroup(@RequestBody TaskGroup taskGroup) {
        taskGroupService.saveGroup(taskGroup);
        return Result.ok();
    }

    @PostMapping("/group/modify")
    public Result<?> modifyGroup(@RequestBody TaskGroup taskGroup) {
        taskGroupService.saveGroup(taskGroup);
        return Result.ok();
    }

    @PostMapping("/group/dels")
    public Result<?> deleteGroups(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<?> ids = (List<?>) params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.fail("参数不能为空");
        }
        List<Long> idList = new ArrayList<>();
        for (Object id : ids) {
            if (id instanceof Number) {
                idList.add(((Number) id).longValue());
            } else {
                try {
                    idList.add(Long.parseLong(id.toString()));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        taskGroupService.deleteGroups(idList);
        return Result.ok();
    }

    // ========== 流程相关接口 ==========

    @GetMapping("/find/trans")
    public Result<?> findTrans(
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false) String type) {

        Long categoriesId = null;
        if (groupId != null && !groupId.isEmpty()) {
            try {
                categoriesId = Long.parseLong(groupId);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        Page<Transformation> page = transformationService.listPage(pageIndex, pageSize, categoriesId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @GetMapping("/find/trans/no/group")
    public Result<?> findTransNoGroup(
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false) String type) {

        Page<Transformation> page = transformationService.listPage(pageIndex, pageSize, null);

        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @GetMapping("/find/yjTrans")
    public Result<?> findYjTrans(@RequestParam(required = false) Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("records", new ArrayList<>());
        result.put("total", 0);
        return Result.ok(result);
    }

    @PostMapping("/delete")
    public Result<?> deleteTask(@RequestParam(required = false) Long id) {
        if (id != null) {
            transformationService.removeById(id);
        }
        return Result.ok();
    }

    @PostMapping("/trans/get")
    public Result<?> getTrans(@RequestParam(required = false) Long id) {
        if (id == null) {
            return Result.ok(new HashMap<>());
        }
        Transformation trans = transformationService.getById(id);
        if (trans != null) {
            trans.setTransformationId(trans.getId());
            trans.setDirectoryName("transformation/" + trans.getId());
        }
        return Result.ok(trans);
    }

    @PostMapping("/group/list")
    public Result<?> groupList() {
        List<TaskGroup> tree = taskGroupService.getGroupTree();
        return Result.ok(tree);
    }

    @PostMapping("/group/del")
    public Result<?> groupDel(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<?> ids = (List<?>) params.get("ids");
        if (ids != null && !ids.isEmpty()) {
            List<Long> idList = new ArrayList<>();
            for (Object id : ids) {
                if (id instanceof Number) {
                    idList.add(((Number) id).longValue());
                }
            }
            taskGroupService.deleteGroups(idList);
        }
        return Result.ok();
    }

    // ========== 计划相关接口 ==========

    @PostMapping("/plan/import/task/node")
    public Result<?> importTaskNode(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/plan/task/node/list")
    public Result<?> taskNodeList() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/plan/map/info/{taskId}")
    public Result<?> mapInfo(@PathVariable Long taskId) {
        return Result.ok(new HashMap<>());
    }

    @PostMapping("/plan/save/both")
    public Result<?> saveBoth(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @DeleteMapping("/plan/delete/both")
    public Result<?> deleteBoth(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/plan/upload/file")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile[] files) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            List<Map<String, Object>> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();
                String savedName = UUID.randomUUID() + "_" + originalName;
                File dest = new File(dir, savedName);
                file.transferTo(dest);

                Map<String, Object> item = new HashMap<>();
                item.put("fileFullName", originalName);
                item.put("filePath", dest.getAbsolutePath());
                fileList.add(item);
            }
            return Result.ok(fileList);
        } catch (IOException e) {
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/plan/node/data/sourceAndFile/save")
    public Result<?> sourceAndFileSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/plan/node/data/source/find")
    public Result<?> sourceFind() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/plan/node/tree")
    public Result<?> getPlanNodeTree() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/plan/download/file")
    public Result<?> downloadFile(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PutMapping("/compiling/{id}")
    public Result<?> compiling(@PathVariable Long id) {
        return Result.ok();
    }

    @PutMapping("/may/compile/{id}")
    public Result<?> mayCompile(@PathVariable Long id) {
        return Result.ok();
    }
}
