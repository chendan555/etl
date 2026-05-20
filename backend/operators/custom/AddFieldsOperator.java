package com.gx.etl.operators.custom;


import com.gx.etl.operator.core.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 示例算子：两个字段值相加
 *
 * 使用说明：
 * 1. 将此文件上传到"算子组件管理" -> "新增组件" -> "上传算子"
 * 2. 系统会自动编译并注册此算子
 * 3. 在流程设计页面可以拖拽使用此算子
 */
public class AddFieldsOperator extends AbstractOperator {

    /**
     * getId() - 返回算子的唯一标识符
     * 这个ID用于在流程中识别算子，必须全局唯一
     * 前端流程图中每个节点的pluginId就是这个值
     */
    @Override
    public String getId() {
        return "AddFields";
    }

    /**
     * getName() - 返回算子的显示名称
     * 这个名称会显示在前端算子列表和流程图节点上
     */
    @Override
    public String getName() {
        return "字段相加";
    }

    /**
     * getCategory() - 返回算子所属分类
     * 可选值：
     *   "input"     - 输入类算子（如读取Excel、CSV、数据库）
     *   "output"    - 输出类算子（如写入Excel、CSV、数据库）
     *   "transform" - 转换类算子（如过滤、计算、排序）
     *   "script"    - 脚本类算子（如JavaScript代码执行）
     * 这个分类决定了算子在前端侧边栏中的分组位置
     */
    @Override
    public String getCategory() {
        return "transform";
    }

    /**
     * getSchema() - 返回算子的参数配置Schema
     * 前端会根据这个Schema动态生成配置表单
     *
     * 支持的参数类型（PropertyType）：
     *   STRING        - 文本输入框
     *   NUMBER        - 数字输入框
     *   BOOLEAN       - 复选框
     *   SELECT        - 下拉选择框（需要配合options使用）
     *   TEXTAREA      - 多行文本框
     *   CODE          - 代码编辑器
     *   TABLE_COLUMN_MAP - 表格列映射
     *
     * property()方法参数说明：
     *   参数1: key - 参数的key，用于在execute中获取值
     *   参数2: label - 参数的显示名称
     *   参数3: type - 参数类型
     *   参数4: required - 是否必填（可选，默认false）
     */
    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("field1", "字段1", OperatorSchema.PropertyType.STRING, true)
                .property("field2", "字段2", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    /**
     * execute() - 算子的核心执行逻辑
     *
     * 参数说明：
     *   ctx   - 执行上下文，包含：
     *           - ctx.getString("key")  获取String类型参数值
     *           - ctx.getInt("key")     获取Integer类型参数值
     *           - ctx.getLong("key")    获取Long类型参数值
     *           - ctx.getBoolean("key") 获取Boolean类型参数值
     *           - ctx.get("key")        获取Object类型参数值
     *           - ctx.getConfig()       获取所有参数的Map
     *           - ctx.getDataSource(id) 获取数据源连接
     *
     *   input - 输入数据集，包含：
     *           - input.getColumnNames() 获取所有列名
     *           - input.iterator()       遍历所有行
     *           - input.collect()        将所有行收集到List
     *           - input.filter(predicate) 过滤行
     *           - input.count()          获取总行数
     *           - input.isEmpty()        是否为空
     *
     * 返回值：
     *   DataSet - 输出数据集，传递给下一个算子
     *   DataSet.of(columns, rows) - 创建新的数据集
     *   DataSet.empty() - 创建空数据集
     *   直接返回input表示透传（不做任何处理）
     *
     * DataRow操作：
     *   row.get("fieldName")    获取字段值
     *   row.put("fieldName", value) 设置字段值
     *   row.copy()              复制一行（深拷贝）
     *   row.hasField("name")    判断字段是否存在
     *   row.remove("name")      删除字段
     *   row.getFields()         获取所有字段的Map
     */
    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // 从配置中获取参数
        String field1 = ctx.getString("field1");
        String field2 = ctx.getString("field2");
        String resultField = ctx.getString("resultField");

        // 参数校验
        if (field1 == null || field2 == null || resultField == null) {
            return input; // 参数不完整，直接透传
        }

        // 获取输入数据的列名，并添加结果列
        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) {
            columns.add(resultField);
        }

        // 遍历每一行，执行相加逻辑
        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy(); // 复制原行
            Object val1 = row.get(field1);
            Object val2 = row.get(field2);

            // 尝试数值相加
            double sum = 0;
            try {
                if (val1 != null) sum += Double.parseDouble(val1.toString());
                if (val2 != null) sum += Double.parseDouble(val2.toString());
            } catch (NumberFormatException e) {
                // 如果不是数字，执行字符串拼接
                newRow.put(resultField, String.valueOf(val1) + String.valueOf(val2));
                rows.add(newRow);
                continue;
            }

            // 整数结果去掉小数点
            if (sum == Math.floor(sum) && !Double.isInfinite(sum)) {
                newRow.put(resultField, (long) sum);
            } else {
                newRow.put(resultField, sum);
            }
            rows.add(newRow);
        }

        // 返回新的数据集
        return DataSet.of(columns, rows);
    }

    /**
     * requiresMaterializedInput() - 是否需要物化输入数据
     *
     * 默认返回false，表示流式处理（逐行处理，内存友好）
     * 返回true表示需要先将所有数据加载到内存（适用于排序、去重、关联等需要全局数据的操作）
     *
     * 一般情况下不需要重写此方法，除非你的算子需要：
     * - 排序（需要所有数据才能排序）
     * - 去重（需要所有数据才能判断重复）
     * - 关联/Join（需要两份数据都在内存中）
     * - 全局统计（如求平均值、最大值、最小值）
     */
    // @Override
    // public boolean requiresMaterializedInput() {
    //     return true;
    // }
}