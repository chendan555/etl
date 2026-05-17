package com.gx.etl.operator.core;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry of all operators. Auto-discovers @Component annotated operators.
 */
@Component
public class OperatorRegistry {

    private final Map<String, AbstractOperator> operatorMap = new LinkedHashMap<>();
    private final List<AbstractOperator> operatorList;

    public OperatorRegistry(List<AbstractOperator> operators) {
        this.operatorList = operators;
    }

    @PostConstruct
    public void init() {
        for (AbstractOperator op : operatorList) {
            operatorMap.put(op.getId(), op);
        }
    }

    public AbstractOperator getOperator(String id) {
        AbstractOperator op = operatorMap.get(id);
        if (op == null) {
            // 未注册的算子返回通用透传算子
            op = new GenericPassThroughOperator(id);
            operatorMap.put(id, op);
        }
        return op;
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
