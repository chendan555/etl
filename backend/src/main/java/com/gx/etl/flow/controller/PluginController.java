package com.gx.etl.flow.controller;

import com.gx.etl.common.Result;
import com.gx.etl.operator.core.AbstractOperator;
import com.gx.etl.operator.core.OperatorRegistry;
import com.gx.etl.operator.core.OperatorSchema;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trans/plugin")
public class PluginController {

    private final OperatorRegistry operatorRegistry;

    public PluginController(OperatorRegistry operatorRegistry) {
        this.operatorRegistry = operatorRegistry;
    }

    /**
     * Get operator schema (JSON Schema for config form)
     */
    @GetMapping("/schema")
    public Result<?> getSchema(@RequestParam String pluginId) {
        AbstractOperator op = operatorRegistry.getOperator(pluginId);
        if (op == null) {
            return Result.fail("未知算子: " + pluginId);
        }
        OperatorSchema schema = op.getSchema();
        return Result.ok(schema);
    }

    /**
     * Get field type list (for column config dropdowns)
     */
    @GetMapping("/find/field/type")
    public Result<?> findFieldType() {
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(typeMap("String", "字符串"));
        types.add(typeMap("Integer", "整数"));
        types.add(typeMap("Long", "长整数"));
        types.add(typeMap("Double", "浮点数"));
        types.add(typeMap("Date", "日期"));
        types.add(typeMap("Boolean", "布尔"));
        types.add(typeMap("BigDecimal", "高精度小数"));
        return Result.ok(types);
    }

    /**
     * Get all databases (for operator config dropdowns)
     */
    @GetMapping("/check/table")
    public Result<?> checkTable(@RequestParam String databaseInfo, @RequestParam String name) {
        return Result.ok(false);
    }

    private Map<String, Object> typeMap(String name, String label) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", label);
        m.put("value", name);
        return m;
    }
}
