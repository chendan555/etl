package com.gx.etl.flow.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gx.etl.common.Result;
import com.gx.etl.flow.entity.OperatorCategory;
import com.gx.etl.flow.entity.OperatorType;
import com.gx.etl.flow.mapper.OperatorCategoryMapper;
import com.gx.etl.flow.mapper.OperatorTypeMapper;
import com.gx.etl.operator.core.AbstractOperator;
import com.gx.etl.operator.core.DynamicOperatorCompiler;
import com.gx.etl.operator.core.OperatorRegistry;
import com.gx.etl.operator.core.OperatorSchema;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plugin/manage")
public class PluginManageController {

    private final OperatorCategoryMapper operatorCategoryMapper;
    private final OperatorTypeMapper operatorTypeMapper;
    private final OperatorRegistry operatorRegistry;
    private final DynamicOperatorCompiler dynamicOperatorCompiler;

    public PluginManageController(OperatorCategoryMapper operatorCategoryMapper, OperatorTypeMapper operatorTypeMapper, OperatorRegistry operatorRegistry, DynamicOperatorCompiler dynamicOperatorCompiler) {
        this.operatorCategoryMapper = operatorCategoryMapper;
        this.operatorTypeMapper = operatorTypeMapper;
        this.operatorRegistry = operatorRegistry;
        this.dynamicOperatorCompiler = dynamicOperatorCompiler;
    }

    /**
     * 获取算子分类树
     */
    @GetMapping("/categories/tree")
    public Result<?> categoriesTree() {
        try {
            List<OperatorCategory> categories = operatorCategoryMapper.selectList(
                    new LambdaQueryWrapper<OperatorCategory>().orderByAsc(OperatorCategory::getSortOrder));
            if (categories == null) categories = new ArrayList<>();

            List<Map<String, Object>> tree = new ArrayList<>();
            for (OperatorCategory cat : categories) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", cat.getId());
            node.put("name", cat.getName());
            node.put("icon", cat.getIcon());
            node.put("sortOrder", cat.getSortOrder());
            node.put("type", cat.getType());

            // 获取该分类下的算子
            List<OperatorType> operators = operatorTypeMapper.selectList(
                    new LambdaQueryWrapper<OperatorType>()
                            .eq(OperatorType::getCategoryId, cat.getId())
                            .orderByAsc(OperatorType::getSortOrder));

            List<Map<String, Object>> children = new ArrayList<>();
            for (OperatorType op : operators) {
                Map<String, Object> child = new HashMap<>();
                child.put("id", op.getId());
                child.put("name", op.getName());
                child.put("pluginId", op.getPluginId());
                child.put("icon", op.getIcon());
                child.put("dragIcon", op.getDragIcon());
                child.put("description", op.getDescription());
                child.put("status", op.getStatus());
                child.put("type", op.getType());
                children.add(child);
            }
            node.put("children", children);
            tree.add(node);
        }
        return Result.ok(tree);
        } catch (Exception e) {
            return Result.ok(new ArrayList<>());
        }
    }

    /**
     * 获取算子列表（支持分页和筛选）
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String name,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String count) {
        try {
            boolean needCount = "true".equals(count);

            // 查询所有分类
            List<OperatorCategory> categories = operatorCategoryMapper.selectList(
                    new LambdaQueryWrapper<OperatorCategory>().orderByAsc(OperatorCategory::getSortOrder));
            if (categories == null) categories = new ArrayList<>();

            // 查询所有算子
            LambdaQueryWrapper<OperatorType> wrapper = new LambdaQueryWrapper<>();
            if (name != null && !name.isEmpty()) {
                wrapper.like(OperatorType::getName, name);
            }
            if (type != null && !type.isEmpty()) {
                try {
                    wrapper.eq(OperatorType::getType, Integer.parseInt(type));
                } catch (NumberFormatException e) { /* ignore */ }
            }
            wrapper.orderByAsc(OperatorType::getSortOrder);
            List<OperatorType> operators = operatorTypeMapper.selectList(wrapper);
            if (operators == null) operators = new ArrayList<>();

            // 按分类分组
            Map<String, List<OperatorType>> grouped = operators.stream()
                    .collect(Collectors.groupingBy(OperatorType::getCategoryId));

            // 构建树结构
            List<Map<String, Object>> tree = new ArrayList<>();
            for (OperatorCategory cat : categories) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", cat.getId());
                node.put("text", cat.getName());
                node.put("name", cat.getName());
                node.put("icon", cat.getIcon());
                node.put("sortOrder", cat.getSortOrder());
                node.put("type", cat.getType());

                List<OperatorType> catOps = grouped.getOrDefault(cat.getId(), Collections.emptyList());
                if (needCount) {
                    node.put("count", catOps.size());
                }

                List<Map<String, Object>> children = new ArrayList<>();
                for (OperatorType op : catOps) {
                    Map<String, Object> child = new HashMap<>();
                    child.put("id", op.getId());
                    child.put("text", op.getName());
                    child.put("name", op.getName());
                    child.put("pluginId", op.getPluginId());
                    child.put("icon", op.getIcon());
                    child.put("dragIcon", op.getDragIcon());
                    child.put("description", op.getDescription());
                    child.put("remark", op.getDescription());
                    child.put("sortOrder", op.getSortOrder());
                    child.put("idIndex", op.getSortOrder());
                    child.put("parentName", cat.getName());
                    child.put("status", op.getStatus());
                    child.put("type", op.getType());
                    children.add(child);
                }
                node.put("children", children);
                node.put("childs", new ArrayList<>());
                tree.add(node);
            }
            return Result.ok(tree);
        } catch (Exception e) {
            return Result.ok(new ArrayList<>());
        }
    }

    /**
     * 新增组件（前端"新增组件"按钮调用）
     */
    @PostMapping("/init")
    public Result<?> initPlugin(@RequestBody Map<String, Object> params) {
        try {
            OperatorType op = new OperatorType();
            String pluginId = params.get("pluginId") != null ? params.get("pluginId").toString() : null;
            String text = params.get("text") != null ? params.get("text").toString() : null;
            String categoriesId = params.get("categoriesId") != null ? params.get("categoriesId").toString() : null;

            if (pluginId == null || pluginId.isEmpty()) {
                return Result.fail("组件编码不能为空");
            }

            op.setId(pluginId);
            op.setPluginId(pluginId);
            op.setName(text);
            op.setCategoryId(categoriesId);
            op.setDom("[]");
            op.setImplementation(params.get("metaClass") != null ? params.get("metaClass").toString() : "");
            op.setDescription(params.get("remark") != null ? params.get("remark").toString() : "");
            op.setIcon("");
            op.setDragIcon("");
            op.setSortOrder(params.get("idIndex") != null ? Integer.parseInt(params.get("idIndex").toString()) : 0);
            op.setType(1);
            op.setStatus(1);

            if (operatorTypeMapper.selectById(op.getId()) != null) {
                operatorTypeMapper.updateById(op);
            } else {
                operatorTypeMapper.insert(op);
            }
            return Result.ok(op);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 保存自定义算子
     */
    @PostMapping("/saveCustomPlugin")
    public Result<?> saveCustomPlugin(@RequestBody Map<String, Object> params) {
        OperatorType op = new OperatorType();
        if (params.get("id") != null) op.setId(params.get("id").toString());
        if (params.get("name") != null) op.setName(params.get("name").toString());
        if (params.get("pluginId") != null) op.setPluginId(params.get("pluginId").toString());
        if (params.get("categoryId") != null) op.setCategoryId(params.get("categoryId").toString());
        if (params.get("icon") != null) op.setIcon(params.get("icon").toString());
        if (params.get("dragIcon") != null) op.setDragIcon(params.get("dragIcon").toString());
        if (params.get("description") != null) op.setDescription(params.get("description").toString());
        if (params.get("implementation") != null) op.setImplementation(params.get("implementation").toString());
        op.setStatus(1);
        op.setSortOrder(0);
        op.setType(1);

        if (op.getId() != null && operatorTypeMapper.selectById(op.getId()) != null) {
            operatorTypeMapper.updateById(op);
        } else {
            operatorTypeMapper.insert(op);
        }
        return Result.ok(op);
    }

    /**
     * 重新上传 .java 算子源文件（保留原有配置）
     */
    @PostMapping("/reUploadOperator")
    public Result<?> reUploadOperator(@RequestParam("file") MultipartFile file,
                                       @RequestParam String id) {
        try {
            if (file.isEmpty()) {
                return Result.fail("请上传文件");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".java")) {
                return Result.fail("请上传 .java 文件");
            }

            // 查询原有算子
            OperatorType existing = operatorTypeMapper.selectById(id);
            if (existing == null) {
                return Result.fail("算子不存在");
            }

            // 读取文件内容
            String sourceCode = new String(file.getBytes(), "UTF-8");

            // 编译并注册
            DynamicOperatorCompiler.CompileResult result = dynamicOperatorCompiler.compileAndRegister(sourceCode);
            if (!result.isSuccess()) {
                return Result.fail(result.getMessage());
            }

            // 更新原有记录（保留dom、categoryId等配置）
            existing.setImplementation(result.getClassName());
            operatorTypeMapper.updateById(existing);

            Map<String, Object> data = new HashMap<>();
            data.put("pluginId", result.getPluginId());
            data.put("name", result.getName());
            data.put("className", result.getClassName());
            return Result.ok(data);

        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传 .java 算子源文件并动态编译注册
     */
    @PostMapping("/uploadOperator")
    public Result<?> uploadOperator(@RequestParam("file") MultipartFile file,
                                     @RequestParam(required = false) String pluginId,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String categoryId,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false, defaultValue = "0") Integer sortOrder) {
        try {
            if (file.isEmpty()) {
                return Result.fail("请上传文件");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".java")) {
                return Result.fail("请上传 .java 文件");
            }

            // 读取文件内容
            String sourceCode = new String(file.getBytes(), "UTF-8");

            // 编译并注册
            DynamicOperatorCompiler.CompileResult result = dynamicOperatorCompiler.compileAndRegister(sourceCode);
            if (!result.isSuccess()) {
                return Result.fail(result.getMessage());
            }

            // 使用编译后提取的 pluginId
            String finalPluginId = result.getPluginId();

            // 检查 pluginId 是否已存在
            List<OperatorType> existing = operatorTypeMapper.selectList(
                    new LambdaQueryWrapper<OperatorType>().eq(OperatorType::getPluginId, finalPluginId));
            if (existing != null && !existing.isEmpty()) {
                return Result.fail("算子编码 " + finalPluginId + " 已存在");
            }

            // 保存到数据库
            OperatorType op = new OperatorType();
            op.setId(finalPluginId);
            op.setPluginId(finalPluginId);
            op.setName(name != null && !name.isEmpty() ? name : result.getName());
            op.setCategoryId(categoryId);
            op.setImplementation(result.getClassName());
            op.setDescription(description != null ? description : "");
            op.setIcon("");
            op.setDragIcon("");
            op.setSortOrder(sortOrder);
            op.setType(1);
            op.setStatus(1);
            op.setDom("[]");
            operatorTypeMapper.insert(op);

            Map<String, Object> data = new HashMap<>();
            data.put("pluginId", finalPluginId);
            data.put("name", result.getName());
            data.put("category", result.getCategory());
            data.put("className", result.getClassName());
            return Result.ok(data);

        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取算子详情
     */
    @GetMapping("/type/{id}/detail")
    public Result<?> typeDetail(@PathVariable String id) {
        try {
            OperatorType op = operatorTypeMapper.selectById(id);
            if (op == null) {
                // 尝试按pluginId查询
                List<OperatorType> list = operatorTypeMapper.selectList(
                        new LambdaQueryWrapper<OperatorType>().eq(OperatorType::getPluginId, id));
                if (list != null && !list.isEmpty()) {
                    op = list.get(0);
                }
            }
            if (op == null) {
                // 如果是step格式的ID（如step0_1），从数据库获取信息
                if (id != null && id.startsWith("step")) {
                    // 从数据库生成的树中查找
                    List<Map<String, Object>> tree = getOperatorTreeFromDb();
                    for (Map<String, Object> category : tree) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> children = (List<Map<String, Object>>) category.get("children");
                        if (children != null) {
                            for (Map<String, Object> child : children) {
                                if (id.equals(child.get("id"))) {
                                    String pluginId = (String) child.get("pluginId");
                                    // 从数据库获取算子详情
                                    List<OperatorType> list = operatorTypeMapper.selectList(
                                            new LambdaQueryWrapper<OperatorType>().eq(OperatorType::getPluginId, pluginId));
                                    if (list != null && !list.isEmpty()) {
                                        OperatorType opFromDb = list.get(0);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", id);
                                        data.put("text", opFromDb.getName());
                                        data.put("name", opFromDb.getName());
                                        data.put("pluginId", pluginId);
                                        data.put("icon", opFromDb.getIcon());
                                        data.put("dragIcon", opFromDb.getDragIcon());
                                        data.put("description", opFromDb.getDescription());
                                        data.put("implementation", opFromDb.getImplementation());
                                        // 使用数据库中的dom
                                        String dom = opFromDb.getDom();
                                        if (dom == null || dom.isEmpty() || "null".equals(dom)) {
                                            // 如果数据库没有dom，尝试从OperatorRegistry生成
                                            AbstractOperator operator = operatorRegistry.getOperator(pluginId);
                                            if (operator != null) {
                                                OperatorSchema schema = operator.getSchema();
                                                dom = schemaToDom(schema);
                                            }
                                        }
                                        data.put("dom", dom);
                                        return Result.ok(data);
                                    }
                                }
                            }
                        }
                    }
                    // 如果找不到，返回基本信息
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", id);
                    data.put("text", "");
                    data.put("name", "");
                    data.put("pluginId", id);
                    data.put("icon", "");
                    data.put("dragIcon", "");
                    data.put("description", "");
                    data.put("implementation", "");
                    data.put("dom", "");
                    return Result.ok(data);
                }
                return Result.fail("算子不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", op.getId());
            data.put("text", op.getName());
            data.put("name", op.getName());
            data.put("pluginId", op.getPluginId());
            data.put("icon", op.getIcon());
            data.put("dragIcon", op.getDragIcon());
            data.put("description", op.getDescription());
            data.put("implementation", op.getImplementation());
            // 如果dom为空或'null'，尝试从OperatorRegistry生成
            String dom = op.getDom();
            if (dom == null || dom.isEmpty() || "null".equals(dom)) {
                AbstractOperator operator = operatorRegistry.getOperator(op.getPluginId());
                if (operator != null) {
                    OperatorSchema schema = operator.getSchema();
                    dom = schemaToDom(schema);
                }
            }
            // 修复showCondition循环依赖问题
            dom = fixShowCondition(dom);
            data.put("dom", dom);
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail("获取详情失败: " + e.getMessage());
        }
    }

    /**
     * 将OperatorSchema转换为前端DOM配置格式
     */
    private String schemaToDom(OperatorSchema schema) {
        if (schema == null) return "[]";
        try {
            List<Map<String, Object>> eleList = new ArrayList<>();
            if (schema.getProperties() != null) {
                for (OperatorSchema.Property prop : schema.getProperties()) {
                    Map<String, Object> ele = new LinkedHashMap<>();
                    ele.put("key", propToComponentKey(prop.getType()));
                    Map<String, Object> config = new LinkedHashMap<>();
                    config.put("name", prop.getLabel());
                    config.put("value", "");
                    config.put("valueKey", new ArrayList<>());
                    config.put("width", "100%");
                    config.put("labelWidth", 150);
                    config.put("isShowFormLabel", true);
                    config.put("span", 24);
                    config.put("choosed", false);
                    config.put("events", new ArrayList<>());
                    config.put("isChooseValueKey", true);
                    config.put("showCondition", new ArrayList<>());
                    // 对于select类型，添加options
                    if (prop.getType() == OperatorSchema.PropertyType.SELECT) {
                        // 使用property自带的options，或为空
                        List<Map<String, Object>> optionsList = new ArrayList<>();
                        if (prop.getOptions() != null) {
                            for (OperatorSchema.Option opt : prop.getOptions()) {
                                Map<String, Object> optMap = new LinkedHashMap<>();
                                optMap.put("name", opt.getLabel());
                                optMap.put("value", opt.getValue());
                                optionsList.add(optMap);
                            }
                        }
                        config.put("options", optionsList);
                        config.put("optionsConfig", createOptionsConfig());
                        config.put("isHttpGetOptions", prop.getHttpUrl() != null && !prop.getHttpUrl().isEmpty());
                        config.put("httpUrl", prop.getHttpUrl() != null ? prop.getHttpUrl() : "");
                        config.put("params", "{}");
                        config.put("multiple", false);
                    }
                    // 对于textarea或code类型
                    if (prop.getType() == OperatorSchema.PropertyType.TEXTAREA ||
                        prop.getType() == OperatorSchema.PropertyType.CODE) {
                        config.put("rows", 4);
                    }
                    ele.put("config", config);
                    eleList.add(ele);
                }
            }
            return com.alibaba.fastjson.JSON.toJSONString(eleList);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String propToComponentKey(OperatorSchema.PropertyType type) {
        switch (type) {
            case STRING: return "input";
            case NUMBER: return "number";
            case BOOLEAN: return "checkbox";
            case SELECT: return "select";
            case TEXTAREA: return "textarea";
            case CODE: return "code";
            case TABLE_COLUMN_MAP: return "table";
            default: return "input";
        }
    }

    private Map<String, Object> createOptionsConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("name", "name");
        config.put("value", "value");
        return config;
    }

    /**
     * 修复showCondition循环依赖问题
     * 如果字段的showCondition引用了自己的valueKey，则移除该条件
     */
    private String fixShowCondition(String dom) {
        if (dom == null || dom.isEmpty() || "null".equals(dom)) return dom;
        try {
            List<Object> eleList = JSON.parseArray(dom, Object.class);
            if (eleList == null) return dom;
            for (Object eleObj : eleList) {
                if (!(eleObj instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> ele = (Map<String, Object>) eleObj;
                Object configObj = ele.get("config");
                if (!(configObj instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> config = (Map<String, Object>) configObj;
                Object valueKeyObj = config.get("valueKey");
                Object showConditionObj = config.get("showCondition");
                if (!(showConditionObj instanceof List)) continue;
                @SuppressWarnings("unchecked")
                List<Object> showCondition = (List<Object>) showConditionObj;
                if (showCondition.isEmpty()) continue;
                // 获取字段的valueKey
                String fieldKey = null;
                if (valueKeyObj instanceof String) {
                    fieldKey = (String) valueKeyObj;
                } else if (valueKeyObj instanceof List && !((List<?>) valueKeyObj).isEmpty()) {
                    Object first = ((List<?>) valueKeyObj).get(0);
                    if (first instanceof String) {
                        fieldKey = (String) first;
                    }
                }
                // 如果showCondition引用了自己的valueKey，则移除该条件
                if (fieldKey != null) {
                    List<Object> newCondition = new ArrayList<>();
                    for (Object condObj : showCondition) {
                        if (!(condObj instanceof Map)) continue;
                        @SuppressWarnings("unchecked")
                        Map<String, Object> condition = (Map<String, Object>) condObj;
                        String condKey = (String) condition.get("key");
                        if (!fieldKey.equals(condKey)) {
                            newCondition.add(condition);
                        }
                    }
                    config.put("showCondition", newCondition);
                }
                // 递归处理tabs中的eleList
                if ("tabs".equals(ele.get("key"))) {
                    Object tabsConfigObj = ele.get("config");
                    if (tabsConfigObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> tabsConfig = (Map<String, Object>) tabsConfigObj;
                        Object tabEleListObj = tabsConfig.get("eleList");
                        if (tabEleListObj instanceof List) {
                            tabsConfig.put("eleList", JSON.parseArray(fixShowCondition(JSON.toJSONString(tabEleListObj)), Object.class));
                        }
                    }
                }
            }
            return JSON.toJSONString(eleList);
        } catch (Exception e) {
            return dom;
        }
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
     * 编辑算子（保存设计）
     */
    @PostMapping("/type/edit")
    public Result<?> typeEdit(@RequestBody Map<String, Object> params) {
        try {
            String id = params.get("id") != null ? params.get("id").toString() : null;
            if (id == null) {
                return Result.fail("id不能为空");
            }
            OperatorType op = operatorTypeMapper.selectById(id);
            if (op == null) {
                return Result.fail("算子不存在");
            }
            if (params.get("dom") != null) {
                op.setDom(params.get("dom").toString());
            }
            if (params.get("name") != null) {
                op.setName(params.get("name").toString());
            }
            if (params.get("description") != null) {
                op.setDescription(params.get("description").toString());
            }
            operatorTypeMapper.updateById(op);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail("保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取算子表单字段（从算子的 getSchema() 动态读取）
     */
    @GetMapping("/getDeclaredFields")
    public Map<String, Object> getDeclaredFields(@RequestParam String pluginID) {
        List<Map<String, Object>> content = new ArrayList<>();
        try {
            // 先按 pluginId 从注册表找算子
            AbstractOperator operator = operatorRegistry.getOperator(pluginID);
            // 找不到再按数据库 ID 查 pluginId
            if (operator == null) {
                OperatorType op = operatorTypeMapper.selectById(pluginID);
                if (op != null && op.getPluginId() != null) {
                    operator = operatorRegistry.getOperator(op.getPluginId());
                }
            }
            if (operator == null) {
                // 按 pluginId 模糊查
                List<OperatorType> list = operatorTypeMapper.selectList(
                        new LambdaQueryWrapper<OperatorType>().eq(OperatorType::getPluginId, pluginID));
                if (list != null && !list.isEmpty()) {
                    operator = operatorRegistry.getOperator(list.get(0).getPluginId());
                }
            }
            if (operator != null) {
                OperatorSchema schema = operator.getSchema();
                if (schema != null && schema.getProperties() != null) {
                    for (OperatorSchema.Property prop : schema.getProperties()) {
                        Map<String, Object> field = new HashMap<>();
                        field.put("key", prop.getKey());
                        field.put("label", prop.getLabel());
                        field.put("type", prop.getType().name().toLowerCase());
                        field.put("required", prop.isRequired());
                        content.add(field);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        return result;
    }

    /**
     * 新增目录
     */
    @PostMapping("/add")
    public Result<?> addCategory(@RequestBody Map<String, Object> params) {
        try {
            OperatorCategory cat = new OperatorCategory();
            cat.setId(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20));
            cat.setName(params.get("text") != null ? params.get("text").toString() : "");
            cat.setType(1);
            cat.setSortOrder(params.get("idIndex") != null ? Integer.parseInt(params.get("idIndex").toString()) : 0);
            cat.setIcon("");
            operatorCategoryMapper.insert(cat);
            return Result.ok(cat);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 编辑目录
     */
    @PostMapping("/edit")
    public Result<?> editCategory(@RequestBody Map<String, Object> params) {
        try {
            String id = params.get("id") != null ? params.get("id").toString() : null;
            if (id == null) return Result.fail("id不能为空");
            OperatorCategory cat = operatorCategoryMapper.selectById(id);
            if (cat == null) return Result.fail("目录不存在");
            if (params.get("text") != null) cat.setName(params.get("text").toString());
            if (params.get("idIndex") != null) cat.setSortOrder(Integer.parseInt(params.get("idIndex").toString()));
            operatorCategoryMapper.updateById(cat);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除目录
     */
    @PostMapping("/remove")
    public Result<?> removeCategory(@RequestParam String id) {
        try {
            operatorCategoryMapper.deleteById(id);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取目录详情
     */
    @GetMapping("/{id}/detail")
    public Result<?> categoryDetail(@PathVariable String id) {
        try {
            OperatorCategory cat = operatorCategoryMapper.selectById(id);
            if (cat == null) return Result.fail("目录不存在");
            Map<String, Object> data = new HashMap<>();
            data.put("id", cat.getId());
            data.put("text", cat.getName());
            data.put("code", cat.getType());
            data.put("idIndex", cat.getSortOrder());
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除算子
     */
    @PostMapping("/type/remove")
    public Result<?> removeType(@RequestParam String id) {
        try {
            operatorTypeMapper.deleteById(id);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
