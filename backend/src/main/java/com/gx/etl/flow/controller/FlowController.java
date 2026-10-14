package com.gx.etl.flow.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.flow.entity.ExecutionLog;
import com.gx.etl.flow.entity.OperatorCategory;
import com.gx.etl.flow.entity.OperatorType;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.mapper.OperatorCategoryMapper;
import com.gx.etl.flow.mapper.OperatorTypeMapper;
import com.gx.etl.flow.service.FlowExecutionEngine;
import com.gx.etl.flow.service.DagParser;
import com.gx.etl.flow.service.FlowChartNode;
import com.gx.etl.flow.service.TransformationService;
import com.gx.etl.flow.service.BatchExecutionService;
import com.gx.etl.operator.core.DataRow;
import com.gx.etl.operator.core.DataSet;
import com.gx.etl.operator.core.OperatorRegistry;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transNew")
public class FlowController {

    @Resource
    private TransformationService transformationService;

    @Resource
    private DataSource dataSource;

    @Resource
    private FlowExecutionEngine executionEngine;

    @Resource
    private OperatorRegistry operatorRegistry;

    @Resource
    private OperatorCategoryMapper operatorCategoryMapper;

    @Resource
    private OperatorTypeMapper operatorTypeMapper;

    @Resource
    private BatchExecutionService batchExecutionService;

    /**
     * Get operator list grouped by category (for sidebar)
     */
    @PostMapping("/steps")
    public Result<?> getSteps(@RequestParam(required = false, defaultValue = "1") Integer type) {
        List<Map<String, Object>> tree = getOperatorTreeFromDb();
        // Wrap in the format frontend expects: data.content
        Map<String, Object> content = new HashMap<>();
        content.put("content", tree);
        return Result.ok(content);
    }

    /**
     * 从数据库获取算子树结构
     */
    private List<Map<String, Object>> getOperatorTreeFromDb() {
        // 查询所有分类
        List<OperatorCategory> categories = operatorCategoryMapper.selectList(
                new LambdaQueryWrapper<OperatorCategory>().orderByAsc(OperatorCategory::getSortOrder));
        if (categories == null) categories = new ArrayList<>();

        // 查询所有启用的算子
        List<OperatorType> operators = operatorTypeMapper.selectList(
                new LambdaQueryWrapper<OperatorType>()
                        .eq(OperatorType::getStatus, 1)
                        .orderByAsc(OperatorType::getSortOrder));
        if (operators == null) operators = new ArrayList<>();

        // 按分类分组
        Map<String, List<OperatorType>> grouped = operators.stream()
                .collect(Collectors.groupingBy(OperatorType::getCategoryId));

        // 构建树结构
        List<Map<String, Object>> result = new ArrayList<>();
        int catIndex = 0;
        for (OperatorCategory cat : categories) {
            List<OperatorType> catOps = grouped.getOrDefault(cat.getId(), Collections.emptyList());
            if (catOps.isEmpty()) continue;

            Map<String, Object> category = new LinkedHashMap<>();
            category.put("id", "category" + catIndex);
            category.put("text", cat.getName());
            category.put("icon", cat.getIcon() != null ? cat.getIcon() : "");
            category.put("cls", "nav-node");

            List<Map<String, Object>> children = new ArrayList<>();
            int stepIndex = 0;
            for (OperatorType op : catOps) {
                Map<String, Object> step = new LinkedHashMap<>();
                step.put("id", "step" + catIndex + "_" + stepIndex);
                step.put("text", op.getName());
                step.put("icon", op.getIcon() != null ? op.getIcon() : "");
                step.put("dragIcon", op.getDragIcon() != null ? op.getDragIcon() : "");
                step.put("cls", "nav");
                step.put("pluginId", op.getPluginId());
                step.put("qtip", op.getName());
                step.put("leaf", true);
                step.put("type", op.getType() != null ? op.getType() : 1);
                step.put("parentName", cat.getName());
                step.put("idIndex", stepIndex);
                step.put("status", op.getStatus());
                step.put("categoriesId", "category" + catIndex);
                step.put("dom", op.getDom() != null ? op.getDom() : "");
                step.put("metaClass", op.getImplementation() != null ? op.getImplementation() : "");
                children.add(step);
                stepIndex++;
            }

            category.put("children", children);
            result.add(category);
            catIndex++;
        }
        return result;
    }

    /**
     * Save flow chart JSON
     */
    @PostMapping("/saveJSON")
    public Result<?> saveJSON(@RequestParam String nodes,
                              @RequestParam String endpoints,
                              @RequestParam String edges,
                              @RequestParam String head,
                              @RequestParam(required = false) Integer transformationId,
                              @RequestParam(required = false) String path,
                              @RequestParam(required = false) String imgUrl) {
        // Reconstruct the full flow JSON
        Map<String, Object> flowJson = new LinkedHashMap<>();
        flowJson.put("nodes", JSON.parseArray(nodes));
        flowJson.put("endpoints", JSON.parseArray(endpoints));
        flowJson.put("edges", JSON.parseArray(edges));
        flowJson.put("head", head);
        String flowJsonStr = JSON.toJSONString(flowJson);

        Long id;

        if (transformationId != null && transformationId > 0) {
            // 更新时保留原有的名称，不从 nodes 中提取
            Transformation existing = transformationService.getById(transformationId.longValue());
            String name = existing != null ? existing.getName() : extractFlowName(nodes);
            transformationService.updateTransformation(transformationId.longValue(), name, flowJsonStr, imgUrl, null);
            id = transformationId.longValue();
        } else {
            // 新建时从 nodes 中提取名称
            String name = extractFlowName(nodes);
            id = transformationService.saveTransformation(name, null, flowJsonStr, imgUrl, null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("transformationId", id);
        result.put("path", "transformation/" + id);
        return Result.ok(result);
    }

    /**
     * Load flow chart JSON
     */
    @PostMapping("/openNew")
    public Result<?> openNew(@RequestParam String path,
                             @RequestParam(required = false, defaultValue = "transformation") String type) {
        Long transId = extractIdFromPath(path);
        if (transId == null) {
            return Result.fail("无效的路径: " + path);
        }

        Transformation trans = transformationService.getById(transId);
        if (trans == null) {
            return Result.fail("流程不存在: " + transId);
        }

        Map<String, Object> content = new HashMap<>();
        if (trans.getFlowJson() != null && !trans.getFlowJson().isEmpty()) {
            Map<String, Object> parsed = JSON.parseObject(trans.getFlowJson(), Map.class);
            if (parsed != null) {
                content = parsed;
            }
        }
        // Ensure required fields always exist
        if (!content.containsKey("nodes")) content.put("nodes", new ArrayList<>());
        if (!content.containsKey("endpoints")) content.put("endpoints", new ArrayList<>());
        if (!content.containsKey("edges")) content.put("edges", new ArrayList<>());
        if (!content.containsKey("head")) content.put("head", "");
        content.put("transformationId", trans.getId());
        content.put("name", trans.getName());
        content.put("bindTableNames", trans.getBindTableNames() != null ? trans.getBindTableNames() : "");

        // Wrap in the format frontend expects
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        return Result.ok(result);
    }

    /**
     * Execute a flow
     */
    @PostMapping("/run")
    public Result<?> run(@RequestParam String nodes,
                         @RequestParam String endpoints,
                         @RequestParam String edges,
                         @RequestParam String head,
                         @RequestParam(required = false) Integer transformationId,
                         @RequestParam(required = false) String path,
                         @RequestParam(required = false, defaultValue = "transformation") String type,
                         @RequestParam(required = false) String params) {
        String flowJson = buildFlowJson(nodes, endpoints, edges, head);
        String transName = extractFlowName(nodes);

        // 解析参数
        Map<String, Object> paramMap = null;
        if (params != null && !params.trim().isEmpty()) {
            paramMap = JSON.parseObject(params, Map.class);
            System.out.println("[run] 收到执行参数: " + paramMap);
        } else {
            System.out.println("[run] 未收到执行参数, params=" + params);
            // 无参数时，清除 ${xxx} 占位符，把 = '${xxx}' 替换为 IS NOT NULL，查全部数据
            flowJson = flowJson.replaceAll("=\\s*'?\\$\\{[^}]+}'?", " IS NOT NULL");
        }

        ExecutionLog log = null;
        if (transformationId != null && transformationId > 0) {
            log = transformationService.createExecutionLog(transformationId.longValue(), transName);
        }

        try {
            Long transId = (transformationId != null && transformationId > 0) ? transformationId.longValue() : null;
            FlowExecutionEngine.ExecutionResult result = executionEngine.executeAndStore(flowJson, transId, paramMap);

            if (log != null) {
                transformationService.updateExecutionLog(log, 1, null, result.getTotalRows());
            }

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("rowCount", result.getTotalRows());
            return Result.ok(data);
        } catch (Exception e) {
            if (log != null) {
                transformationService.updateExecutionLog(log, 2, e.getMessage(), 0);
            }
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 批量执行 - 一次传入多组参数，后端循环执行
     * 只需传 transformationId + params（List<Map>）
     */
    @PostMapping("/batchRun")
    public Result<?> batchRun(@RequestParam Long transformationId,
                              @RequestParam String params) {
        // 1. 从数据库加载流程 JSON
        Transformation trans = transformationService.getById(transformationId);
        if (trans == null || trans.getFlowJson() == null || trans.getFlowJson().isEmpty()) {
            return Result.fail("流程不存在或未保存: " + transformationId);
        }

        // 2. 解析参数列表
        List<Map<String, String>> paramList = JSON.parseObject(params, new com.alibaba.fastjson.TypeReference<List<Map<String, String>>>(){});

        if (paramList == null || paramList.isEmpty()) {
            return Result.fail("参数列表为空");
        }

        // 3. 生成 taskId 并启动异步执行
        String taskId = UUID.randomUUID().toString();
        System.out.println("[batchRun] transformationId=" + transformationId + ", 批次数=" + paramList.size() + ", taskId=" + taskId);

        // 4. 启动异步执行
        batchExecutionService.executeBatchAsync(taskId, transformationId, paramList);

        // 5. 立即返回 taskId
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        data.put("totalCount", paramList.size());
        data.put("message", "批量任务已启动，请通过 WebSocket 接收进度");
        return Result.ok(data);
    }

    /**
     * 查询批量执行进度（WebSocket 失败时的 HTTP 轮询兜底）
     */
    @GetMapping("/batchProgress")
    public Result<?> getBatchProgress(@RequestParam String taskId) {
        com.gx.etl.flow.websocket.ProgressMessage progress = batchExecutionService.getLatestProgress(taskId);
        if (progress == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("status", "unknown");
            data.put("message", "尚无进度或任务不存在");
            return Result.ok(data);
        }
        return Result.ok(progress);
    }

    /**
     * 取消批量执行任务
     */
    @PostMapping("/cancelBatch")
    public Result<?> cancelBatch(@RequestParam String taskId) {
        batchExecutionService.cancelTask(taskId);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        data.put("message", "取消请求已发送");
        return Result.ok(data);
    }

    /**
     * Preview data at a specific node
     */
    @PostMapping("/previewData")
    public Result<?> previewData(@RequestParam String nodeId,
                                  @RequestParam(required = false, defaultValue = "1") int pageIndex,
                                  @RequestParam(required = false, defaultValue = "100") int pageSize,
                                  @RequestParam String nodes,
                                  @RequestParam String endpoints,
                                  @RequestParam String edges,
                                  @RequestParam String head,
                                  @RequestParam(required = false) Integer transformationId,
                                  @RequestParam(required = false) String path,
                                  @RequestParam(required = false, defaultValue = "transformation") String type) {
        String flowJson = buildFlowJson(nodes, endpoints, edges, head);

        try {
            FlowExecutionEngine.ExecutionResult result = executionEngine.execute(flowJson, nodeId);

            List<Map<String, Object>> rows = new ArrayList<>();
            List<String> columns = new ArrayList<>();

            if (result.getDataSet() != null) {
                columns = result.getDataSet().getColumnNames();
                int skip = (pageIndex - 1) * pageSize;
                int count = 0;
                for (DataRow row : result.getDataSet()) {
                    if (count < skip) { count++; continue; }
                    if (rows.size() >= pageSize) break;
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    for (String col : columns) {
                        rowData.put(col, row.get(col));
                    }
                    rows.add(rowData);
                    count++;
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("columns", columns);
            data.put("rows", rows);
            data.put("total", result.getTotalRows());
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * Get execution logs for a transformation
     */
    @PostMapping("/log/page")
    public Result<?> logPage(@RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int pageIndex,
                              @RequestParam(defaultValue = "10") int pageSize) {
        Page<ExecutionLog> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<ExecutionLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(ExecutionLog::getTransName, keyword);
        }
        wrapper.orderByDesc(ExecutionLog::getCreateTime);
        Page<ExecutionLog> result = transformationService.getExecutionLogPage(page, wrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        return Result.ok(data);
    }

    /**
     * Get all steps for a transformation (used by frontend for field discovery)
     */
    @PostMapping("/previousSteps")
    public Result<?> previousSteps(@RequestParam String nodes,
                                    @RequestParam String edges,
                                    @RequestParam(required = false) String currentNodeId,
                                    @RequestParam(required = false) String nodeId) {
        // Return list of steps that come before currentNodeId
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/prevSteps")
    public Result<?> prevSteps(@RequestParam String nodes,
                                @RequestParam String edges,
                                @RequestParam(required = false) String currentNodeId,
                                @RequestParam(required = false) String nodeId,
                                @RequestParam(required = false) String endpoints,
                                @RequestParam(required = false) String head) {
        String targetNodeId = (nodeId != null && !nodeId.isEmpty()) ? nodeId : currentNodeId;
        if (targetNodeId == null || targetNodeId.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }
        try {
            String flowJson = buildFlowJson(nodes, endpoints != null ? endpoints : "[]", edges, head);
            Map<String, FlowChartNode> nodeMap = DagParser.parse(flowJson);
            FlowChartNode targetNode = nodeMap.get(targetNodeId);
            if (targetNode == null || targetNode.getUpstreamIds().isEmpty()) {
                return Result.ok(new ArrayList<>());
            }
            List<Map<String, Object>> steps = new ArrayList<>();
            for (String upstreamId : targetNode.getUpstreamIds()) {
                FlowChartNode upstreamNode = nodeMap.get(upstreamId);
                if (upstreamNode != null) {
                    Map<String, Object> step = new LinkedHashMap<>();
                    step.put("name", upstreamNode.getName());
                    step.put("value", upstreamNode.getName());
                    steps.add(step);
                }
            }
            return Result.ok(steps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/prevStepsFileds")
    public Result<?> prevStepsFields(@RequestParam String nodes,
                                      @RequestParam String edges,
                                      @RequestParam(required = false) String endpoints,
                                      @RequestParam(required = false) String currentNodeId,
                                      @RequestParam(required = false) String nodeId,
                                      @RequestParam(required = false) String head,
                                      @RequestParam(required = false) Integer transformationId,
                                      @RequestParam(required = false) String path,
                                      @RequestParam(required = false, defaultValue = "transformation") String type) {
        String targetNodeId = (nodeId != null && !nodeId.isEmpty()) ? nodeId : currentNodeId;
        System.out.println("[prevStepsFileds] targetNodeId=" + targetNodeId + ", nodesLen=" + (nodes != null ? nodes.length() : 0) + ", edgesLen=" + (edges != null ? edges.length() : 0));
        if (targetNodeId == null || targetNodeId.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }
        try {
            String flowJson = buildFlowJson(nodes, endpoints != null ? endpoints : "[]", edges, head);
            // 清除 ${xxx} 参数占位符，避免查询返回空结果
            // 把 = '${xxx}' 替换为 = ''，再用 OR 1=1 保证能查到数据
            flowJson = flowJson.replaceAll("=\\s*'?\\$\\{[^}]+}'?", " IS NOT NULL");
            // 找到目标节点的上游节点，执行到上游节点获取字段
            Map<String, FlowChartNode> nodeMap = DagParser.parse(flowJson);
            FlowChartNode targetNode = nodeMap.get(targetNodeId);
            System.out.println("[prevStepsFileds] targetNode=" + (targetNode != null ? targetNode.getName() : "null") + ", upstreamIds=" + (targetNode != null ? targetNode.getUpstreamIds() : "[]"));
            String execNodeId = targetNodeId;
            if (targetNode != null && !targetNode.getUpstreamIds().isEmpty()) {
                String upstreamId = targetNode.getUpstreamIds().get(0);
                FlowChartNode upstreamNode = nodeMap.get(upstreamId);
                System.out.println("[prevStepsFileds] upstreamNode=" + (upstreamNode != null ? upstreamNode.getName() + "(" + upstreamNode.getPluginId() + ")" : "null"));
                execNodeId = upstreamId;
            }
            System.out.println("[prevStepsFileds] executing flow up to node: " + execNodeId);
            FlowExecutionEngine.ExecutionResult result = executionEngine.execute(flowJson, execNodeId);
            if (result != null && result.getDataSet() != null) {
                List<String> columns = result.getDataSet().getColumnNames();
                System.out.println("[prevStepsFileds] execNodeId=" + execNodeId + ", got columns: " + columns + ", rowCount=" + result.getDataSet().count());
                List<Map<String, Object>> fields = new ArrayList<>();
                for (String col : columns) {
                    Map<String, Object> field = new LinkedHashMap<>();
                    field.put("name", col);
                    field.put("value", col);
                    field.put("code", col);
                    field.put("key", col);
                    field.put("type", "VARCHAR");
                    fields.add(field);
                }
                return Result.ok(fields);
            } else {
                System.out.println("[prevStepsFileds] result or dataSet is null");
            }
        } catch (Exception e) {
            System.out.println("[prevStepsFileds] error: " + e.getMessage());
            e.printStackTrace();
            // 执行失败时返回空列表
        }
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/getStepFieldsByName")
    public Result<?> getStepFieldsByName(@RequestParam String nodes,
                                         @RequestParam String edges,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String endpoints,
                                         @RequestParam(required = false) String head,
                                         @RequestParam(required = false) Integer transformationId,
                                         @RequestParam(required = false) String path,
                                         @RequestParam(required = false, defaultValue = "transformation") String type) {
        if (name == null || name.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }
        try {
            String flowJson = buildFlowJson(nodes, endpoints != null ? endpoints : "[]", edges, head);
            flowJson = flowJson.replaceAll("=\\s*'?\\$\\{[^}]+}'?", " IS NOT NULL");
            Map<String, FlowChartNode> nodeMap = DagParser.parse(flowJson);
            // 按名称查找节点
            String targetId = null;
            for (Map.Entry<String, FlowChartNode> entry : nodeMap.entrySet()) {
                if (name.equals(entry.getValue().getName())) {
                    targetId = entry.getKey();
                    break;
                }
            }
            if (targetId == null) {
                return Result.ok(new ArrayList<>());
            }
            FlowExecutionEngine.ExecutionResult result = executionEngine.execute(flowJson, targetId);
            if (result != null && result.getDataSet() != null) {
                List<String> columns = result.getDataSet().getColumnNames();
                List<Map<String, Object>> fields = new ArrayList<>();
                for (String col : columns) {
                    Map<String, Object> field = new LinkedHashMap<>();
                    field.put("name", col);
                    field.put("field", col);
                    field.put("value", col);
                    fields.add(field);
                }
                return Result.ok(fields);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/nextSteps")
    public Result<?> nextSteps(@RequestParam String nodes,
                                @RequestParam String edges,
                                @RequestParam(required = false) String currentNodeId,
                                @RequestParam(required = false) String nodeId) {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/addStepName")
    public Result<?> addStepName(@RequestParam(required = false) String stepName,
                                  @RequestParam(required = false) String nodes,
                                  @RequestParam(required = false) String nodeData) {
        // Auto-increment step name
        if (stepName == null || stepName.isEmpty()) {
            // Try to extract name from nodeData
            if (nodeData != null && !nodeData.isEmpty()) {
                try {
                    Map<String, Object> node = JSON.parseObject(nodeData, Map.class);
                    if (node != null && node.containsKey("text")) {
                        stepName = (String) node.get("text");
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            if (stepName == null || stepName.isEmpty()) {
                stepName = "步骤";
            }
        }
        return Result.ok(stepName);
    }

    @PostMapping("/updateStepName")
    public Result<?> updateStepName(@RequestParam(required = false) String nodeId,
                                     @RequestParam(required = false) String stepName) {
        if (stepName == null) stepName = "";
        return Result.ok(stepName);
    }

    /**
     * 获取文件/目录树列表（用于前端文件选择树）
     * 前端 req_config.js: 文件目录 treeConfig { label: 'path', isLazyLoad: true }
     * 前端 api/index.ts: getFilesName() 调用 GET /api/transNew/files?path=/
     *
     * @param path 目录路径；Windows 下传入 "/" 或空时返回盘符列表
     */
    @RequestMapping(value = "/files", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> files(@RequestParam(required = false) String path) {
        try {
            // 判断是否为 Windows 系统
            boolean isWindows = File.separatorChar == '\\';

            // path 为空或 "/" 时，返回根目录级别
            if (path == null || path.isEmpty() || "/".equals(path) || "\\".equals(path)) {
                if (isWindows) {
                    // Windows: 返回盘符列表
                    return Result.ok(getWindowsDriveRoots());
                } else {
                    // Linux/Mac: 返回根目录 "/"
                    return Result.ok(listFiles("/"));
                }
            }

            // 指定路径：列出该目录下的子文件和子目录
            return Result.ok(listFiles(path));
        } catch (Exception e) {
            return Result.ok(new ArrayList<>());
        }
    }

    /**
     * 获取 Windows 盘符列表（C:\, D:\, ...）
     */
    private List<Map<String, Object>> getWindowsDriveRoots() {
        List<Map<String, Object>> result = new ArrayList<>();
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                String path = root.getAbsolutePath(); // e.g. "C:\"
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("path", path);
                item.put("name", path);
                item.put("isDir", true);
                item.put("children", true); // has children (for lazy loading)
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 列出指定目录下的文件和子目录
     */
    private List<Map<String, Object>> listFiles(String dirPath) {
        List<Map<String, Object>> result = new ArrayList<>();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return result;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return result;
        }
        // 排序：目录在前，文件在后；各自按名称排序
        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });
        for (File f : files) {
            // 排除隐藏文件和系统文件（Windows 下排除 $Recycle.Bin, System Volume Information 等）
            if (f.isHidden()) continue;
            String name = f.getName();
            if (name.startsWith("$")) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("path", f.getAbsolutePath());
            item.put("name", name);
            item.put("isDir", f.isDirectory());
            item.put("children", f.isDirectory()); // directory can be expanded
            if (!f.isDirectory()) {
                item.put("size", f.length());
                item.put("lastModified", f.lastModified());
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 预览文件
     */
    @PostMapping("/previewFile")
    public Result<?> previewFile() {
        return Result.ok(new HashMap<>());
    }

    /**
     * 获取表字段
     */
    @PostMapping("/tableFields")
    public Result<?> tableFields() {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 预览输出数据
     */
    @PostMapping("/previewDataOut")
    public Result<?> previewDataOut() {
        return Result.ok(new HashMap<>());
    }

    /**
     * 获取数据视图
     */
    @PostMapping("/getDataView")
    public Result<?> getDataView(@RequestParam(required = false) String key) {
        Map<String, Object> data = new HashMap<>();
        // 从执行结果中获取数据
        if (executionEngine != null) {
            FlowExecutionEngine.ExecutionResult execResult = null;
            if (key != null && !key.isEmpty()) {
                try {
                    execResult = executionEngine.getLastResult(Long.parseLong(key));
                } catch (NumberFormatException ignored) {}
            }
            if (execResult == null) {
                execResult = executionEngine.getLatestResult();
            }
            if (execResult != null && execResult.getDataSet() != null) {
                List<String> colNames = execResult.getDataSet().getColumnNames();
                // 构建columns（前端期望 {lable, header} 格式）
                List<Map<String, Object>> columns = new ArrayList<>();
                for (String col : colNames) {
                    Map<String, Object> colMap = new LinkedHashMap<>();
                    colMap.put("lable", col);
                    colMap.put("header", col);
                    columns.add(colMap);
                }
                // 构建firstRecords
                List<Map<String, Object>> records = new ArrayList<>();
                int count = 0;
                for (com.gx.etl.operator.core.DataRow row : execResult.getDataSet()) {
                    if (count >= 100) break;
                    Map<String, Object> record = new LinkedHashMap<>();
                    for (String col : colNames) {
                        record.put(col, row.get(col));
                    }
                    records.add(record);
                    count++;
                }
                data.put("columns", columns);
                data.put("firstRecords", records);
                return Result.ok(data);
            }
        }
        data.put("columns", new ArrayList<>());
        data.put("firstRecords", new ArrayList<>());
        return Result.ok(data);
    }

    /**
     * 获取执行结果
     */
    @PostMapping("/result")
    public Result<?> result(@RequestParam(required = false) String transformationId) {
        Map<String, Object> data = new HashMap<>();
        if (transformationId != null && !transformationId.isEmpty()) {
            try {
                Long transId = Long.parseLong(transformationId);
                FlowExecutionEngine.ExecutionResult execResult = executionEngine.getLastResult(transId);
                if (execResult != null) {
                    data.put("log", execResult.getLog());
                    data.put("stepMeasure", execResult.getStepMetrics());
                    data.put("stepStatus", execResult.getStepStatusList());
                    return Result.ok(data);
                }
            } catch (NumberFormatException ignored) {}
        }
        data.put("log", "");
        data.put("stepMeasure", new ArrayList<>());
        data.put("stepStatus", new ArrayList<>());
        return Result.ok(data);
    }

    /**
     * 获取输入输出字段
     */
    @PostMapping("/iputOutputFields")
    public Result<?> iputOutputFields() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/iputOutputFieldsName")
    public Result<?> iputOutputFieldsName() {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 获取字段和行数据
     */
    @PostMapping("/getFieldAndRow")
    public Result<?> getFieldAndRow() {
        return Result.ok(new HashMap<>());
    }

    /**
     * 获取内容值
     */
    @PostMapping("/contendValue")
    public Result<?> contendValue() {
        return Result.ok(new ArrayList<>());
    }

    /**
     * 获取字段类型
     */
    @PostMapping("/fieldType")
    public Result<?> fieldType() {
        return Result.ok(new HashMap<>());
    }

    /**
     * 下载Excel
     */
    @GetMapping("/downloadExcel")
    public Result<?> downloadExcel(@RequestParam(required = false) String path) {
        return Result.ok();
    }

    /**
     * 计算描述
     */
    @PostMapping("/calcDesc")
    public Result<?> calcDesc() {
        return Result.ok(new HashMap<>());
    }

    /**
     * 获取数量
     */
    @PostMapping("/get/count")
    public Result<?> getCount() {
        Map<String, Object> result = new HashMap<>();
        result.put("count", 0);
        return Result.ok(result);
    }

    /**
     * 删除评估
     */
    @PostMapping("/assess/delete")
    public Result<?> assessDelete() {
        return Result.ok();
    }

    /**
     * 运行评估
     */
    @PostMapping("/assess/run")
    public Result<?> assessRun() {
        return Result.ok(new HashMap<>());
    }

    /**
     * 规则分页
     */
    @PostMapping("/rule/page")
    public Result<?> rulePage() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", new ArrayList<>());
        result.put("total", 0);
        return Result.ok(result);
    }

    /**
     * 删除日志
     */
    @GetMapping("/log/del")
    public Result<?> logDel(@RequestParam(required = false) Long id) {
        return Result.ok();
    }

    // ==================== 规则相关 ====================

    @PostMapping("/rule/save")
    public Result<?> ruleSave() {
        return Result.ok();
    }

    @PostMapping("/rule/delete")
    public Result<?> ruleDelete() {
        return Result.ok();
    }

    @PostMapping("/rule/find")
    public Result<?> ruleFind() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/rule/findTypeList")
    public Result<?> ruleFindTypeList() {
        return Result.ok(new ArrayList<>());
    }

    // ==================== 评估相关 ====================

    @PostMapping("/assess/find")
    public Result<?> assessFind() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/assess/save")
    public Result<?> assessSave() {
        return Result.ok();
    }

    @PostMapping("/assess/page")
    public Result<?> assessPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    // ==================== 审核相关 ====================

    @PostMapping("/audit/page")
    public Result<?> auditPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    @PostMapping("/audit/find")
    public Result<?> auditFind() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/audit/auditById")
    public Result<?> auditById() {
        return Result.ok();
    }

    // ==================== 统计相关 ====================

    @GetMapping("/census/auditResult")
    public Result<?> censusAuditResult() {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/census/runResult")
    public Result<?> censusRunResult() {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/census/categroy")
    public Result<?> censusCategroy() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/census/allNums")
    public Result<?> censusAllNums() {
        return Result.ok(0);
    }

    @GetMapping("/census/checkNum")
    public Result<?> censusCheckNum() {
        return Result.ok(0);
    }

    @GetMapping("/errorForm/page")
    public Result<?> errorFormPage() {
        Map<String, Object> r = new HashMap<>();
        r.put("data", new ArrayList<>());
        r.put("total", 0);
        return Result.ok(r);
    }

    // ==================== 日志统计 ====================

    @GetMapping("/log/allNums")
    public Result<?> logAllNums() {
        return Result.ok(0);
    }

    @GetMapping("/log/runResult")
    public Result<?> logRunResult() {
        return Result.ok(new HashMap<>());
    }

    @GetMapping("/get/transformationByTask")
    public Result<?> getTransByTask() {
        return Result.ok(new ArrayList<>());
    }

    // ==================== 绑定表相关 ====================

    /**
     * 获取ETL数据库中的所有表名
     */
    @GetMapping("/bindTables")
    public Result<?> getBindTables() {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME")) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            return Result.fail("获取表列表失败: " + e.getMessage());
        }
        return Result.ok(tables);
    }

    /**
     * 获取指定表的字段列表
     */
    @GetMapping("/bindTableColumns")
    public Result<?> getBindTableColumns(@RequestParam String tableName) {
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.COLUMN_NAME, c.DATA_TYPE, m.COMMENTS FROM USER_TAB_COLUMNS c LEFT JOIN USER_COL_COMMENTS m ON c.TABLE_NAME = m.TABLE_NAME AND c.COLUMN_NAME = m.COLUMN_NAME WHERE c.TABLE_NAME = ? ORDER BY c.COLUMN_ID")) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> col = new LinkedHashMap<>();
                    col.put("name", rs.getString("COLUMN_NAME"));
                    col.put("type", rs.getString("DATA_TYPE"));
                    String comment = rs.getString("COMMENTS");
                    col.put("comment", comment != null ? comment : "");
                    columns.add(col);
                }
            }
        } catch (SQLException e) {
            return Result.fail("获取字段列表失败: " + e.getMessage());
        }
        return Result.ok(columns);
    }

    /**
     * 更新流程绑定的表名
     */
    @PostMapping("/updateBindTables")
    public Result<?> updateBindTables(@RequestParam Long transformationId,
                                       @RequestParam String bindTableNames) {
        transformationService.updateTransformation(transformationId.longValue(), null, null, null, null, bindTableNames);
        return Result.ok();
    }

    /**
     * 一键生成所有算法模型算子的处理流程
     * 参考 GXETL_R_TRANSFORMATION.sql 中的命名规范
     * 每个流程：表输入 → [预处理] → 算法算子 → 表输出
     */
    @PostMapping("/initAlgorithmFlows")
    public Result<?> initAlgorithmFlows() {
        // 流程定义：name, pluginId, 是否需要预处理(字段选择)
        Object[][] algorithms = {
            // 逻辑回归系列
            {"模型-测试3-逻辑回归", "LogisticsRegression", true},
            {"模型-测试3-逻辑回归训练", "LogisticsRegressionTrain", true},
            {"模型-测试3-逻辑回归集成", "TopNJC", true},
            {"模型-测试3-多元逻辑回归", "ManyTopN", true},
            // 朴素贝叶斯系列
            {"模型-测试4-朴素贝叶斯", "NativeBayes", true},
            {"模型-测试4-朴素贝叶斯训练", "NativeBayesTrain", true},
            // 随机森林系列
            {"模型-测试5-随机森林", "RandomForest", true},
            {"模型-测试5-随机森林训练", "RandomForestTrain", true},
            {"模型-测试5-多元随机森林", "ManyRandomForest", true},
            // 线性回归系列
            {"模型-测试6-线性回归", "线性回归", true},
            {"模型-测试6-线性回归训练", "线性回归(训练)", true},
            // 决策树系列
            {"模型-测试7-决策树", "决策树", true},
            {"模型-测试7-决策树训练", "决策树(训练)", true},
            // SVM系列
            {"模型-测试5-SVM", "SVM", true},
            {"模型-测试5-SVM训练", "SVM（训练）", true},
            {"模型-测试5-SVM集成", "SVM（集成）", true},
            // 聚类算法
            {"模型-测试8-KMeans聚类", "K-Means", true},
            {"模型-测试8-DBScan聚类", "DBScan密度聚类算法", true},
            // 数据融合
            {"模型-测试9-多源数据融合", "多源数据融合", false},
            {"模型-测试9-多阶段数据融合", "多阶段数据融合", false},
            {"模型-测试9-多源多阶段融合", "多源多阶段数据融合", false},
            // 数值离散化
            {"模型-测试10-数值离散化", "数值离散化处理", true}
        };

        List<Long> createdIds = new ArrayList<>();
        int index = 0;
        for (Object[] algo : algorithms) {
            String flowName = (String) algo[0];
            String pluginId = (String) algo[1];
            boolean needPreprocess = (Boolean) algo[2];
            index++;

            // 生成所有节点和端口ID
            String inputNodeId = "node-in-" + UUID.randomUUID().toString().substring(0, 8);
            String algoNodeId = "node-algo-" + UUID.randomUUID().toString().substring(0, 8);
            String outputNodeId = "node-out-" + UUID.randomUUID().toString().substring(0, 8);

            String srcPort1 = "sp-" + UUID.randomUUID().toString().substring(0, 8);
            String tgtPort1 = "tp-" + UUID.randomUUID().toString().substring(0, 8);
            String srcPort2 = "sp-" + UUID.randomUUID().toString().substring(0, 8);
            String tgtPort2 = "tp-" + UUID.randomUUID().toString().substring(0, 8);

            // 表输入节点 - 从TEST表读取数据（旧版格式：属性在顶层）
            String inputNode = "{\"id\":\"" + inputNodeId + "\",\"position\":{\"left\":80,\"top\":180},"
                + "\"text\":\"表输入\",\"pluginId\":\"TableInput\",\"nodeId\":\"" + inputNodeId + "\","
                + "\"points\":{\"targets\":[],\"sources\":[\"" + srcPort1 + "\"]},"
                + "\"properties\":{\"ruleForm\":{\"stepName\":\"表输入\",\"connection\":\"2\","
                + "\"sql\":\"SELECT ID, NAME, AGE FROM TEST\"}}}";

            // 算法节点
            String algoNode = "{\"id\":\"" + algoNodeId + "\",\"position\":{\"left\":420,\"top\":180},"
                + "\"text\":\"" + flowName.substring(flowName.lastIndexOf("-") + 1) + "\",\"pluginId\":\"" + pluginId + "\","
                + "\"nodeId\":\"" + algoNodeId + "\","
                + "\"points\":{\"targets\":[\"" + tgtPort1 + "\"],\"sources\":[\"" + srcPort2 + "\"]},"
                + "\"properties\":{\"ruleForm\":{\"stepName\":\"" + flowName.substring(flowName.lastIndexOf("-") + 1) + "\"}}}";

            // 表输出节点
            String outputNode = "{\"id\":\"" + outputNodeId + "\",\"position\":{\"left\":760,\"top\":180},"
                + "\"text\":\"表输出\",\"pluginId\":\"TableOutput\",\"nodeId\":\"" + outputNodeId + "\","
                + "\"points\":{\"targets\":[\"" + tgtPort2 + "\"],\"sources\":[]},"
                + "\"properties\":{\"ruleForm\":{\"stepName\":\"表输出\",\"connection\":\"2\","
                + "\"table\":\"RESULT_" + pluginId.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase() + "\"}}}";

            // 如果需要预处理，插入字段选择节点
            String nodesJson;
            String endpointsJson;
            String edgesJson;

            if (needPreprocess) {
                String preprocessNodeId = "node-prep-" + UUID.randomUUID().toString().substring(0, 8);
                String srcPortPrep = "sp-" + UUID.randomUUID().toString().substring(0, 8);
                String tgtPortPrep = "tp-" + UUID.randomUUID().toString().substring(0, 8);

                String preprocessNode = "{\"id\":\"" + preprocessNodeId + "\",\"position\":{\"left\":250,\"top\":180},"
                    + "\"text\":\"字段选择\",\"pluginId\":\"SelectValues\",\"nodeId\":\"" + preprocessNodeId + "\","
                    + "\"points\":{\"targets\":[\"" + tgtPortPrep + "\"],\"sources\":[\"" + srcPortPrep + "\"]},"
                    + "\"properties\":{\"ruleForm\":{\"stepName\":\"字段选择\"}}}";

                nodesJson = "[" + inputNode + "," + preprocessNode + "," + algoNode + "," + outputNode + "]";
                endpointsJson = "["
                    + "{\"id\":\"" + srcPort1 + "\",\"data\":{\"value\":\"输出\"}},"
                    + "{\"id\":\"" + tgtPortPrep + "\",\"data\":{\"value\":\"输入\"}},"
                    + "{\"id\":\"" + srcPortPrep + "\",\"data\":{\"value\":\"输出\"}},"
                    + "{\"id\":\"" + tgtPort1 + "\",\"data\":{\"value\":\"输入\"}},"
                    + "{\"id\":\"" + srcPort2 + "\",\"data\":{\"value\":\"输出\"}},"
                    + "{\"id\":\"" + tgtPort2 + "\",\"data\":{\"value\":\"输入\"}}"
                    + "]";
                edgesJson = "["
                    + "\"" + srcPort1 + "&&" + tgtPortPrep + "\","
                    + "\"" + srcPortPrep + "&&" + tgtPort1 + "\","
                    + "\"" + srcPort2 + "&&" + tgtPort2 + "\""
                    + "]";
            } else {
                nodesJson = "[" + inputNode + "," + algoNode + "," + outputNode + "]";
                endpointsJson = "["
                    + "{\"id\":\"" + srcPort1 + "\",\"data\":{\"value\":\"输出\"}},"
                    + "{\"id\":\"" + tgtPort1 + "\",\"data\":{\"value\":\"输入\"}},"
                    + "{\"id\":\"" + srcPort2 + "\",\"data\":{\"value\":\"输出\"}},"
                    + "{\"id\":\"" + tgtPort2 + "\",\"data\":{\"value\":\"输入\"}}"
                    + "]";
                edgesJson = "["
                    + "\"" + srcPort1 + "&&" + tgtPort1 + "\","
                    + "\"" + srcPort2 + "&&" + tgtPort2 + "\""
                    + "]";
            }

            // 组装完整 flowJson
            Map<String, Object> flowJson = new LinkedHashMap<>();
            flowJson.put("nodes", JSON.parseArray(nodesJson));
            flowJson.put("endpoints", JSON.parseArray(endpointsJson));
            flowJson.put("edges", JSON.parseArray(edgesJson));
            flowJson.put("head", "");
            String flowJsonStr = JSON.toJSONString(flowJson);

            Long id = transformationService.saveTransformation(flowName, null, flowJsonStr, null, null);
            createdIds.add(id);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", createdIds.size());
        result.put("ids", createdIds);
        result.put("message", "成功创建 " + createdIds.size() + " 个算法处理流程");
        return Result.ok(result);
    }

    // Helper methods

    private String buildFlowJson(String nodes, String endpoints, String edges, String head) {
        Map<String, Object> flowJson = new LinkedHashMap<>();
        flowJson.put("nodes", JSON.parseArray(nodes));
        flowJson.put("endpoints", JSON.parseArray(endpoints));
        flowJson.put("edges", JSON.parseArray(edges));
        flowJson.put("head", head);
        return JSON.toJSONString(flowJson);
    }

    private String extractFlowName(String nodesJson) {
        try {
            List<Object> nodes = JSON.parseArray(nodesJson, Object.class);
            if (nodes != null && !nodes.isEmpty()) {
                // Return first node's text as flow name
                Map<String, Object> first = (Map<String, Object>) nodes.get(0);
                Map<String, Object> data = (Map<String, Object>) first.get("data");
                if (data != null) {
                    return (String) data.getOrDefault("text", "未命名流程");
                }
            }
        } catch (Exception ignored) {}
        return "未命名流程";
    }

    private Long extractIdFromPath(String path) {
        if (path == null) return null;
        // Path format: "transformation/123" or just "123"
        String[] parts = path.split("/");
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
