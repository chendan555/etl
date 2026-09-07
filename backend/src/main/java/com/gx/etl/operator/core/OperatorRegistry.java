package com.gx.etl.operator.core;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gx.etl.flow.entity.OperatorType;
import com.gx.etl.flow.mapper.OperatorTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry of all operators. Auto-discovers @Component annotated operators
 * and dynamically uploaded operators from database.
 */
@Component
public class OperatorRegistry {

    private static final Logger log = LoggerFactory.getLogger(OperatorRegistry.class);
    private static final String CUSTOM_PACKAGE = "com.gx.etl.operators.custom";

    private final Map<String, AbstractOperator> operatorMap = new LinkedHashMap<>();
    private final List<AbstractOperator> operatorList;
    private final OperatorTypeMapper operatorTypeMapper;

    public OperatorRegistry(List<AbstractOperator> operators, OperatorTypeMapper operatorTypeMapper) {
        this.operatorList = operators;
        this.operatorTypeMapper = operatorTypeMapper;
    }

    @PostConstruct
    public void init() {
        // 1. 注册 Spring @Component 算子
        for (AbstractOperator op : operatorList) {
            operatorMap.put(op.getId(), op);
        }

        // 2. 加载数据库中动态上传的算子
        loadDynamicOperators();
    }

    /**
     * 从数据库加载动态上传的算子（com.gx.etl.operators.custom 包下的类）
     */
    private void loadDynamicOperators() {
        try {
            List<OperatorType> ops = operatorTypeMapper.selectList(
                    new LambdaQueryWrapper<OperatorType>()
                            .eq(OperatorType::getStatus, 1)
                            .isNotNull(OperatorType::getImplementation)
                            .ne(OperatorType::getImplementation, "")
                            .ne(OperatorType::getImplementation, "null"));

            // 与 DynamicOperatorCompiler 的输出目录保持一致（绝对路径，锚定 user.dir）
            Path classesDir = DynamicOperatorCompiler.getCustomDir().resolve("classes");
            if (!Files.exists(classesDir)) {
                return;
            }

            URL url = classesDir.toAbsolutePath().toUri().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());

            for (OperatorType op : ops) {
                String impl = op.getImplementation();
                // 只加载 custom 包下的动态算子
                if (impl == null || !impl.startsWith(CUSTOM_PACKAGE + ".")) {
                    continue;
                }
                // 跳过已注册的（Spring @Component 优先）
                if (operatorMap.containsKey(op.getPluginId())) {
                    continue;
                }
                try {
                    Class<?> clazz = classLoader.loadClass(impl);
                    AbstractOperator operator = (AbstractOperator) clazz.newInstance();
                    operatorMap.put(operator.getId(), operator);
                    log.info("动态算子加载成功: pluginId={}, name={}, class={}", operator.getId(), operator.getName(), impl);
                } catch (Exception e) {
                    log.warn("动态算子加载失败: pluginId={}, class={}, error={}", op.getPluginId(), impl, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("加载动态算子失败", e);
        }
    }

    public AbstractOperator getOperator(String id) {
        AbstractOperator op = operatorMap.get(id);
        if (op == null) {
            op = new GenericPassThroughOperator(id);
            operatorMap.put(id, op);
        }
        return op;
    }

    /**
     * 动态注册算子
     */
    public void registerOperator(AbstractOperator op) {
        operatorMap.put(op.getId(), op);
    }

    public Collection<AbstractOperator> getAll() {
        return operatorMap.values();
    }

    /**
     * Get operators grouped by category, formatted for frontend API compatibility.
     */
    public List<Map<String, Object>> getOperatorTree() {
        Map<String, List<AbstractOperator>> grouped = operatorMap.values().stream()
                .collect(Collectors.groupingBy(AbstractOperator::getCategory, LinkedHashMap::new, Collectors.toList()));

        Map<String, String> categoryNames = new LinkedHashMap<>();
        categoryNames.put("input", "输入");
        categoryNames.put("output", "输出");
        categoryNames.put("transform", "转换");
        categoryNames.put("script", "脚本");

        List<Map<String, Object>> result = new ArrayList<>();
        int catIndex = 0;
        for (Map.Entry<String, List<AbstractOperator>> entry : grouped.entrySet()) {
            Map<String, Object> category = new LinkedHashMap<>();
            category.put("id", "category" + catIndex);
            category.put("text", categoryNames.getOrDefault(entry.getKey(), entry.getKey()));
            category.put("icon", "");
            category.put("cls", "nav-node");

            List<Map<String, Object>> children = new ArrayList<>();
            int stepIndex = 0;
            for (AbstractOperator op : entry.getValue()) {
                Map<String, Object> step = new LinkedHashMap<>();
                step.put("id", "step" + catIndex + "_" + stepIndex);
                step.put("text", op.getName());
                step.put("icon", "");
                step.put("dragIcon", "");
                step.put("cls", "nav");
                step.put("pluginId", op.getId());
                step.put("qtip", op.getName());
                step.put("leaf", true);
                step.put("type", 1);
                step.put("parentName", categoryNames.getOrDefault(entry.getKey(), entry.getKey()));
                step.put("idIndex", stepIndex);
                step.put("status", 1);
                step.put("categoriesId", "category" + catIndex);
                step.put("dom", "");
                step.put("metaClass", op.getClass().getName());
                children.add(step);
                stepIndex++;
            }

            category.put("children", children);
            result.add(category);
            catIndex++;
        }
        return result;
    }
}
