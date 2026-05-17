# 缺失接口清单

本文档列出前端调用但后端未实现的所有接口。

## 处理方式

对于缺失的接口，建议按以下优先级处理：

1. **P0**: 核心功能必须实现
2. **P1**: 返回空数据/成功即可 (避免 404)
3. **P2**: 可暂时忽略

---

## 快速修复方案

在各个 Controller 中添加以下空实现，可以避免 404 错误：

### CommonController 需添加

```java
// /api/common/ 下
@GetMapping("/getBatchNumber")
public Result<?> getBatchNumber() { return Result.ok("1"); }

@PostMapping("/receiveData")
public Result<?> receiveData(@RequestBody(required = false) Map<String, Object> p) { return Result.ok(); }

@GetMapping("/getFormwordListList")
public Result<?> getFormwordListList() { return Result.ok(new ArrayList<>()); }
```

### DatabaseController 需添加

```java
// /v1/center/database/ 下
@GetMapping("/page")
public Result<?> page(...) { return listPage(...); }

@PostMapping("/create")
public Result<?> create(@RequestBody Map<String, Object> p) { return save(p); }

@PostMapping("/update")
public Result<?> update(@RequestBody Map<String, Object> p) { return save(p); }

@GetMapping("/back/show/assign")
public Result<?> showAssign() { return Result.ok(new ArrayList<>()); }

@GetMapping("/connect/dm/getViews")
public Result<?> getViews(@RequestParam Long idDatabase) { return Result.ok(new ArrayList<>()); }

@GetMapping("/connect/dm/getTableData")
public Result<?> getTableData(@RequestParam Long idDatabase, @RequestParam String tableName) { 
    return Result.ok(new HashMap<>()); 
}

@PostMapping("/assign/tree")
public Result<?> assignTree() { return Result.ok(new ArrayList<>()); }

@PostMapping("/assign/save")
public Result<?> assignSave(@RequestBody Map<String, Object> p) { return Result.ok(); }

@PostMapping("/task/group/list")
public Result<?> taskGroupList() { return Result.ok(new ArrayList<>()); }

@PostMapping("/task/group/del")
public Result<?> taskGroupDel(@RequestBody Map<String, Object> p) { return Result.ok(); }
```

### FlowController 需添加

```java
// /api/transNew/ 下 - 规则相关
@PostMapping("/rule/save")
public Result<?> ruleSave(@RequestBody Map<String, Object> p) { return Result.ok(); }

@PostMapping("/rule/delete")
public Result<?> ruleDelete(@RequestBody Map<String, Object> p) { return Result.ok(); }

@PostMapping("/rule/find")
public Result<?> ruleFind(@RequestBody Map<String, Object> p) { return Result.ok(new ArrayList<>()); }

@PostMapping("/rule/findTypeList")
public Result<?> ruleFindTypeList() { return Result.ok(new ArrayList<>()); }

// 评估相关
@PostMapping("/assess/find")
public Result<?> assessFind(@RequestBody Map<String, Object> p) { return Result.ok(new ArrayList<>()); }

@PostMapping("/assess/save")
public Result<?> assessSave(@RequestBody Map<String, Object> p) { return Result.ok(); }

@PostMapping("/assess/page")
public Result<?> assessPage(@RequestBody Map<String, Object> p) { 
    Map<String, Object> r = new HashMap<>();
    r.put("data", new ArrayList<>());
    r.put("total", 0);
    return Result.ok(r); 
}

// 审核相关
@PostMapping("/audit/page")
public Result<?> auditPage(@RequestBody Map<String, Object> p) {
    Map<String, Object> r = new HashMap<>();
    r.put("data", new ArrayList<>());
    r.put("total", 0);
    return Result.ok(r);
}

@PostMapping("/audit/find")
public Result<?> auditFind(@RequestBody Map<String, Object> p) { return Result.ok(new ArrayList<>()); }

@PostMapping("/audit/auditById")
public Result<?> auditById(@RequestBody Map<String, Object> p) { return Result.ok(); }

// 统计相关
@GetMapping("/census/auditResult")
public Result<?> censusAuditResult() { return Result.ok(new HashMap<>()); }

@GetMapping("/census/runResult")
public Result<?> censusRunResult() { return Result.ok(new HashMap<>()); }

@GetMapping("/census/categroy")
public Result<?> censusCategroy() { return Result.ok(new ArrayList<>()); }

@GetMapping("/census/allNums")
public Result<?> censusAllNums() { return Result.ok(0); }

@GetMapping("/census/checkNum")
public Result<?> censusCheckNum() { return Result.ok(0); }

@GetMapping("/errorForm/page")
public Result<?> errorFormPage() {
    Map<String, Object> r = new HashMap<>();
    r.put("data", new ArrayList<>());
    r.put("total", 0);
    return Result.ok(r);
}

// 日志统计
@GetMapping("/log/allNums")
public Result<?> logAllNums() { return Result.ok(0); }

@GetMapping("/log/runResult")
public Result<?> logRunResult() { return Result.ok(new HashMap<>()); }

@GetMapping("/get/transformationByTask")
public Result<?> getTransByTask() { return Result.ok(new ArrayList<>()); }
```

### DataSourceController 需添加

```java
// /api/data/source/ 下 - 仿真数据
@GetMapping("/artificial/list")
public Result<?> artificialList() { return Result.ok(new ArrayList<>()); }

@PostMapping("/artificial/save/or/edit")
public Result<?> artificialSave(@RequestBody Map<String, Object> p) { return Result.ok(); }

// 文件消息
@GetMapping("/fileMessage/list/all")
public Result<?> fileMessageList() { return Result.ok(new ArrayList<>()); }

@GetMapping("/fileMessage/download")
public Result<?> fileMessageDownload() { return Result.ok(); }

@PostMapping("/fileMessage/ftp/upload")
public Result<?> ftpUpload() { return Result.ok(); }

@PostMapping("/fileMessage/ftp/record/list")
public Result<?> ftpRecordList() {
    Map<String, Object> r = new HashMap<>();
    r.put("data", new ArrayList<>());
    r.put("total", 0);
    return Result.ok(r);
}
```

---

## 需要新建的 Controller

### 1. TaskController (`/api/task`)

```java
@RestController
@RequestMapping("/api/task")
public class TaskController {
    
    @PostMapping("/group/getGroupList")
    public Result<?> getGroupList() { return Result.ok(new ArrayList<>()); }
    
    @PostMapping("/plan/import/task/node")
    public Result<?> importTaskNode(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @GetMapping("/plan/task/node/list")
    public Result<?> taskNodeList() { return Result.ok(new ArrayList<>()); }
    
    @GetMapping("/plan/map/info/{taskId}")
    public Result<?> mapInfo(@PathVariable Long taskId) { return Result.ok(new HashMap<>()); }
    
    @PostMapping("/plan/save/both")
    public Result<?> saveBoth(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @DeleteMapping("/plan/delete/both")
    public Result<?> deleteBoth(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/plan/upload/file")
    public Result<?> uploadFile() { return Result.ok(); }
    
    @PostMapping("/plan/node/data/sourceAndFile/save")
    public Result<?> sourceAndFileSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @GetMapping("/plan/node/data/source/find")
    public Result<?> sourceFind() { return Result.ok(new ArrayList<>()); }
    
    @PutMapping("/compiling/{id}")
    public Result<?> compiling(@PathVariable Long id) { return Result.ok(); }
    
    @PutMapping("/may/compile/{id}")
    public Result<?> mayCompile(@PathVariable Long id) { return Result.ok(); }
}
```

### 2. AuditController (`/api/audit`)

```java
@RestController
@RequestMapping("/api/audit")
public class AuditController {
    
    @PostMapping("/rule/types")
    public Result<?> ruleTypes() { return Result.ok(new ArrayList<>()); }
    
    @PostMapping("/rule/page")
    public Result<?> rulePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/rule/detail")
    public Result<?> ruleDetail(@RequestBody Map<String, Object> p) { return Result.ok(new HashMap<>()); }
    
    @PostMapping("/rule/create")
    public Result<?> ruleCreate(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/rule/edit")
    public Result<?> ruleEdit(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/rule/audit")
    public Result<?> ruleAudit(@RequestBody Map<String, Object> p) { return Result.ok(); }
}
```

### 3. PlanController (`/api/trans/plan`)

```java
@RestController
@RequestMapping("/api/trans/plan")
public class PlanController {
    
    @GetMapping("/categroy/cache/tree")
    public Result<?> categoryTree() { return Result.ok(new ArrayList<>()); }
    
    @PostMapping("/pageReorganizationByCondition")
    public Result<?> pageReorganization() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/update/reorganization")
    public Result<?> updateReorganization(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @GetMapping("/page/data/query")
    public Result<?> dataQuery() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @GetMapping("/get/out/detail")
    public Result<?> outDetail() { return Result.ok(new HashMap<>()); }
    
    @GetMapping("/getDataType")
    public Result<?> getDataType() { return Result.ok(new ArrayList<>()); }
    
    @GetMapping("/getDataStatus")
    public Result<?> getDataStatus() { return Result.ok(new ArrayList<>()); }
    
    @GetMapping("/getFileType")
    public Result<?> getFileType() { return Result.ok(new ArrayList<>()); }
    
    @GetMapping("/getTransByPlan")
    public Result<?> getTransByPlan() { return Result.ok(new ArrayList<>()); }
    
    @PutMapping("/update/type")
    public Result<?> updateType(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/quit/type")
    public Result<?> quitType(@RequestBody Map<String, Object> p) { return Result.ok(); }
}
```

### 4. RepositoryController (`/api/repository`)

```java
@RestController
@RequestMapping("/api/repository")
public class RepositoryController {
    
    @PutMapping("/new/updateTransToModel")
    public Result<?> updateTransToModel(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/new/addCopyTrans")
    public Result<?> addCopyTrans(@RequestBody Map<String, Object> p) { return Result.ok(); }
}
```

### 5. ClassifyController (`/api/equip/classify`)

```java
@RestController
@RequestMapping("/api/equip/classify")
public class ClassifyController {
    
    @GetMapping("/tree")
    public Result<?> tree() { return Result.ok(new ArrayList<>()); }
    
    @GetMapping("/info")
    public Result<?> info() { return Result.ok(new HashMap<>()); }
    
    @DeleteMapping("/delete")
    public Result<?> delete() { return Result.ok(); }
    
    @PutMapping("/update")
    public Result<?> update(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @GetMapping("/selectMaxId")
    public Result<?> selectMaxId() { return Result.ok(0); }
}
```

### 6. DllController (`/api/dll`)

```java
@RestController
@RequestMapping("/api/dll")
public class DllController {
    
    @PostMapping("/exec/model/save")
    public Result<?> modelSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/exec/list/page")
    public Result<?> listPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/exec/data/file/upload")
    public Result<?> fileUpload() { return Result.ok(); }
    
    @GetMapping("/exec/model/del")
    public Result<?> modelDel() { return Result.ok(); }
    
    @PostMapping("/exec/model/exec")
    public Result<?> modelExec(@RequestBody Map<String, Object> p) { return Result.ok(); }
}
```

### 7. YjController (`/api/yj`)

```java
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
    public Result<?> execTable(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
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
    public Result<?> historyDel() { return Result.ok(); }
    
    @PostMapping("/record/check/page")
    public Result<?> checkPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/jsonData/saveJsonConfig")
    public Result<?> saveJsonConfig(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @GetMapping("/jsonData/queryJsonConfigById")
    public Result<?> queryJsonConfigById() { return Result.ok(new HashMap<>()); }
    
    @GetMapping("/batch/num/generate")
    public Result<?> batchNumGenerate() { return Result.ok("1"); }
    
    @PostMapping("/protocol/page")
    public Result<?> protocolPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/protocol/save")
    public Result<?> protocolSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @DeleteMapping("/protocol/delete")
    public Result<?> protocolDelete() { return Result.ok(); }
    
    @PostMapping("/form/page")
    public Result<?> formPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/form/save")
    public Result<?> formSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @DeleteMapping("/form/delete")
    public Result<?> formDelete() { return Result.ok(); }
    
    @PostMapping("/tableConfig/queryListTableConfig")
    public Result<?> queryListTableConfig() { return Result.ok(new ArrayList<>()); }
    
    @PostMapping("/tableConfig/saveTableConfigAndDetail")
    public Result<?> saveTableConfigAndDetail(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @DeleteMapping("/tableConfig/deleteTableConfigAndDetail")
    public Result<?> deleteTableConfigAndDetail() { return Result.ok(); }
    
    @PostMapping("/tableConfig/getTableConfigList")
    public Result<?> getTableConfigList() { return Result.ok(new ArrayList<>()); }
    
    @GetMapping("/tableConfig/queryTableConfigById")
    public Result<?> queryTableConfigById() { return Result.ok(new HashMap<>()); }
    
    @PostMapping("/config/table/page")
    public Result<?> configTablePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/config/table/delete")
    public Result<?> configTableDelete(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/config/table/save")
    public Result<?> configTableSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/config/file/page")
    public Result<?> configFilePage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/config/file/save")
    public Result<?> configFileSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/config/file/delete")
    public Result<?> configFileDelete(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/config/simulation/page")
    public Result<?> configSimulationPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }
    
    @PostMapping("/config/simulation/save")
    public Result<?> configSimulationSave(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/config/simulation/delete")
    public Result<?> configSimulationDelete(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @PostMapping("/protocol/file/merge")
    public Result<?> protocolFileMerge(@RequestBody Map<String, Object> p) { return Result.ok(); }
    
    @GetMapping("/index/config/merge/num")
    public Result<?> indexMergeNum() { return Result.ok(0); }
    
    @GetMapping("/index/config/exec/situation")
    public Result<?> indexExecSituation() { return Result.ok(new HashMap<>()); }
    
    @GetMapping("/index/cumulative/volume")
    public Result<?> indexCumulativeVolume() { return Result.ok(0); }
}
```

### 8. EsController (`/api/es`)

```java
@RestController
@RequestMapping("/api/es")
public class EsController {
    
    @PostMapping("/imitate/call")
    public Result<?> imitateCall(@RequestBody Map<String, Object> p) { return Result.ok(); }
}
```

### 9. FileController (`/api/file`)

```java
@RestController
@RequestMapping("/api/file")
public class FileController {
    
    @PostMapping("/analysis/context")
    public Result<?> analysisContext(@RequestBody Map<String, Object> p) { return Result.ok(new HashMap<>()); }
    
    @GetMapping("/analysis/whereabouts")
    public Result<?> analysisWhereabouts() { return Result.ok(new HashMap<>()); }
}
```

---

## 总结

按照以上方案，创建 9 个新的 Controller 并添加缺失的空实现，可以解决大部分 404 错误。

**需要新建的 Controller:**
1. TaskController
2. AuditController
3. PlanController
4. RepositoryController
5. ClassifyController
6. DllController
7. YjController
8. EsController
9. FileController

**需要修改的现有 Controller:**
1. CommonController
2. DatabaseController
3. FlowController
4. DataSourceController
