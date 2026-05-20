package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 列处理算子 - 对单列数据做聚合计算（求和/平均/最大值/最小值）
 *
 * 前端配置项（ruleForm）：
 *   fieldName    - 要处理的字段名
 *   processType  - 处理类型: sum/avg/max/min
 *   resultField  - 结果字段名（不填则覆盖原字段）
 *
 * 输出：将聚合结果作为新列追加到每一行，或者替换原字段值
 */
@Component
public class ColumnProcessOperator extends AbstractOperator {

    // ==================== 基础信息 ====================

    /**
     * 算子唯一标识，前端通过这个 ID 找到对应算子
     * 必须和 T_OPERATOR_TYPE 表的 type 字段、前端 TaskTypeConfig 文件夹名一致
     */
    @Override
    public String getId() { return "ColumnProcess"; }

    /**
     * 算子显示名称，界面上看到的名字
     */
    @Override
    public String getName() { return "列处理"; }

    /**
     * 算子分类，决定在左侧工具栏哪个分组下显示
     * transform=转换, input=输入, output=输出, script=脚本
     */
    @Override
    public String getCategory() { return "transform"; }

    // ==================== Schema 定义 ====================

    /**
     * 定义算子的配置项（表单字段）
     * 前端根据这个 schema 自动生成配置表单
     *
     * property(字段key, 显示名, 类型, 是否必填)
     * 类型: STRING=文本框, NUMBER=数字框, SELECT=下拉框, BOOLEAN=开关, TEXTAREA=多行文本
     */
    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "处理字段", OperatorSchema.PropertyType.STRING, true)
                .property("processType", "处理类型", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING);
    }

    // ==================== 核心执行逻辑 ====================

    /**
     * 算子执行入口
     *
     * @param ctx   执行上下文，包含算子配置（ruleForm）和数据源访问
     * @param input 上游算子传入的数据集
     * @return 处理后的数据集，传给下游算子
     *
     * 数据流: 上游 DataSet -> execute() -> 新 DataSet -> 下游
     */
    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // ---- 1. 读取配置 ----
        // ctx.getString("fieldName") 从 ruleForm 中取 fieldName 的值
        String fieldName = ctx.getString("fieldName");
        // 处理类型: sum=求和, avg=平均, max=最大值, min=最小值
        String processType = ctx.getString("processType");
        // 结果字段名，不填则用原字段名
        String resultField = ctx.getString("resultField");

        System.out.println("[ColumnProcess] fieldName=" + fieldName + ", processType=" + processType + ", resultField=" + resultField);
        System.out.println("[ColumnProcess] input columns=" + input.getColumnNames() + ", rowCount=" + input.count());

        // 配置不完整，直接透传数据给下游
        if (fieldName == null || fieldName.isEmpty() || processType == null || processType.isEmpty()) {
            System.out.println("[ColumnProcess] 配置不完整，透传");
            return input;
        }

        // 结果字段默认和原字段同名（覆盖）
        if (resultField == null || resultField.isEmpty()) {
            resultField = fieldName;
        }

        // ---- 2. 第一遍遍历：收集该列所有数值，算出聚合结果 ----
        // 需要遍历所有行才能算 sum/avg/max/min
        double sum = 0;          // 累加和
        int count = 0;           // 有效数值计数（用于算平均）
        double max = Double.MIN_VALUE;  // 最大值，初始设最小
        double min = Double.MAX_VALUE;  // 最小值，初始设最大

        for (DataRow row : input) {
            // row.get(fieldName) 取该行该字段的值
            Object val = row.get(fieldName);
            if (val == null) continue;  // 跳过空值

            // 转成 double，转失败则跳过
            double d;
            try {
                d = Double.parseDouble(val.toString());
            } catch (NumberFormatException e) {
                continue;  // 非数值，跳过
            }

            // 累加
            sum += d;
            count++;
            // 更新最大最小
            if (d > max) max = d;
            if (d < min) min = d;
        }

        // 没有有效数值，直接透传
        if (count == 0) return input;

        // ---- 3. 根据处理类型得出最终结果 ----
        double result;
        switch (processType.toLowerCase()) {
            case "sum":
                result = sum;
                break;
            case "avg":
                result = sum / count;  // 平均值 = 总和 / 数量
                break;
            case "max":
                result = max;
                break;
            case "min":
                result = min;
                break;
            default:
                return input;  // 未知类型，透传
        }

        // ---- 4. 第二遍遍历：把聚合结果写入每一行 ----
        // 输出列 = 原始列 + 结果列（如果结果列是新列）
//        List<String> columns = new ArrayList<>(input.getColumnNames());
//        if (!columns.contains(resultField)) {
//            columns.add(resultField);  // 新列加入列名列表
//        }

        List<String> columns = new ArrayList<String>();
        columns.add(resultField);

        List<DataRow> resultRows = new ArrayList<>();
        DataRow dataRow = new DataRow();
        dataRow.put(resultField,result);
        resultRows.add(dataRow);

//        for (DataRow row : input) {
//            // row.copy() 复制一行，不修改原始数据
//            DataRow newRow = row.copy();
//            // 把聚合结果写入结果字段
//            newRow.put(resultField, result);
//            resultRows.add(newRow);
//        }

        // DataSet.of(列名列表, 行数据列表) 构建新的数据集
        System.out.println("[ColumnProcess] output columns=" + columns + ", resultField=" + resultField + ", result=" + result);
        return DataSet.of(columns, resultRows);
    }
}
