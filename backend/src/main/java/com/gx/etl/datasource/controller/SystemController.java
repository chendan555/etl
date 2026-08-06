package com.gx.etl.datasource.controller;

import com.gx.etl.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 系统通用枚举数据API。
 * 为前端 operator 配置表单的下拉选项提供数据。
 * 所有端点返回静态枚举值，无业务逻辑。
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    /**
     * 获取可用的字符编码列表
     * 前端 req_config.js: 编码 optionsConfig { name: 'name', value: 'name' }
     * 前端 api/index.ts: getAvailableCharsets()
     */
    @PostMapping("/availableCharsets")
    public Result<?> availableCharsets() {
        List<Map<String, String>> list = new ArrayList<>();
        // 常见编码
        String[] charsets = {"UTF-8", "GBK", "GB2312", "ISO-8859-1", "UTF-16", "UTF-16LE", "UTF-16BE",
                "ASCII", "Big5", "Shift_JIS", "EUC-JP", "EUC-KR", "GB18030", "windows-1252"};
        for (String cs : charsets) {
            list.add(createNameMap(cs));
        }
        return Result.ok(list);
    }

    /**
     * 获取文件/目录过滤类型
     * 前端 req_config.js: 获取文件或目录 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getFilterFileType")
    public Result<?> getFilterFileType() {
        return Result.ok(Arrays.asList(
                createTV("文件", "files"),
                createTV("目录", "directories"),
                createTV("文件和目录", "both")
        ));
    }

    /**
     * 获取数据类型格式
     * 前端 req_config.js: 获取数据类型格式 optionsConfig { name: 'name', value: 'name' }
     * params: { valueType: "" }
     */
    @PostMapping("/valueFormat")
    public Result<?> valueFormat(@RequestParam(required = false) String valueType) {
        List<Map<String, String>> all = new ArrayList<>();

        if (valueType == null || valueType.isEmpty() || "String".equalsIgnoreCase(valueType)) {
            all.add(createNameMap("none"));
            all.add(createNameMap("trim"));
            all.add(createNameMap("upper"));
            all.add(createNameMap("lower"));
        }
        if (valueType == null || valueType.isEmpty() || "Number".equalsIgnoreCase(valueType)) {
            all.add(createNameMap("#,##0.00"));
            all.add(createNameMap("#,##0"));
            all.add(createNameMap("0.00"));
            all.add(createNameMap("0"));
            all.add(createNameMap("percent"));
        }
        if (valueType == null || valueType.isEmpty() || "Date".equalsIgnoreCase(valueType)) {
            all.add(createNameMap("yyyy-MM-dd"));
            all.add(createNameMap("yyyy/MM/dd"));
            all.add(createNameMap("yyyyMMdd"));
            all.add(createNameMap("yyyy-MM-dd HH:mm:ss"));
            all.add(createNameMap("yyyy/MM/dd HH:mm:ss"));
            all.add(createNameMap("MM/dd/yyyy"));
            all.add(createNameMap("dd/MM/yyyy"));
        }

        return Result.ok(all);
    }

    /**
     * 获取聚合方式
     * 前端 req_config.js: 获取聚合方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getAggregationType")
    public Result<?> getAggregationType() {
        return Result.ok(Arrays.asList(
                createTV("求和", "sum"),
                createTV("平均值", "average"),
                createTV("计数", "count"),
                createTV("最大值", "max"),
                createTV("最小值", "min"),
                createTV("去重计数", "count_distinct"),
                createTV("标准差", "stddev"),
                createTV("方差", "variance"),
                createTV("累积和", "cumulative_sum"),
                createTV("百分比", "percentile")
        ));
    }

    /**
     * 获取比较符
     * 前端 req_config.js: 获取比较符 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getSQLfun")
    public Result<?> getSQLfun() {
        return Result.ok(Arrays.asList(
                createTV("=", "="),
                createTV("<>", "<>"),
                createTV("<", "<"),
                createTV(">", ">"),
                createTV("<=", "<="),
                createTV(">=", ">="),
                createTV("LIKE", "LIKE"),
                createTV("NOT LIKE", "NOT LIKE"),
                createTV("IS NULL", "IS NULL"),
                createTV("IS NOT NULL", "IS NOT NULL"),
                createTV("IN", "IN"),
                createTV("NOT IN", "NOT IN"),
                createTV("BETWEEN", "BETWEEN")
        ));
    }

    /**
     * 获取大小写转换方式
     * 前端 req_config.js: 获取大小写方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getLowerUpper")
    public Result<?> getLowerUpper() {
        return Result.ok(Arrays.asList(
                createTV("转为小写", "lower"),
                createTV("转为大写", "upper"),
                createTV("首字母大写", "capitalize"),
                createTV("不转换", "none")
        ));
    }

    /**
     * 获取填充方式
     * 前端 req_config.js: 获取填充方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getPadding")
    public Result<?> getPadding() {
        return Result.ok(Arrays.asList(
                createTV("左侧填充", "left"),
                createTV("右侧填充", "right"),
                createTV("两侧填充", "both"),
                createTV("不填充", "none")
        ));
    }

    /**
     * 获取删除特殊字符方式
     * 前端 req_config.js: 获取删除特殊字符方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getRemoveSpecialCharacters")
    public Result<?> getRemoveSpecialCharacters() {
        return Result.ok(Arrays.asList(
                createTV("删除所有特殊字符", "all"),
                createTV("仅保留字母数字", "alphanumeric"),
                createTV("仅保留数字", "digits_only"),
                createTV("仅保留字母", "letters_only"),
                createTV("不删除", "none")
        ));
    }

    /**
     * 获取 XML 转义方式
     * 前端 req_config.js: 获取XML转译方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getmaskXML")
    public Result<?> getmaskXML() {
        return Result.ok(Arrays.asList(
                createTV("转义 XML 特殊字符", "escape_xml"),
                createTV("反转义 XML 实体", "unescape_xml"),
                createTV("CDATA 包裹", "cdata"),
                createTV("不处理", "none")
        ));
    }

    /**
     * 获取字符串中数字处理方式
     * 前端 req_config.js: 获取字符串中数字处理方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getDigits")
    public Result<?> getDigits() {
        return Result.ok(Arrays.asList(
                createTV("仅保留数字", "digits_only"),
                createTV("删除数字", "remove_digits"),
                createTV("仅保留非数字", "non_digits_only"),
                createTV("不处理", "none")
        ));
    }

    /**
     * 获取字体颜色
     * 前端 req_config.js: 获取字体颜色 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getFontColor")
    public Result<?> getFontColor() {
        return Result.ok(Arrays.asList(
                createTV("黑色", "black"),
                createTV("红色", "red"),
                createTV("蓝色", "blue"),
                createTV("绿色", "green"),
                createTV("黄色", "yellow"),
                createTV("白色", "white"),
                createTV("灰色", "gray"),
                createTV("橙色", "orange"),
                createTV("紫色", "purple")
        ));
    }

    /**
     * 获取字体对齐方式
     * 前端 req_config.js: 获取字体对齐方式 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getFontAlignmentr")
    public Result<?> getFontAlignmentr() {
        return Result.ok(Arrays.asList(
                createTV("左对齐", "left"),
                createTV("居中", "center"),
                createTV("右对齐", "right"),
                createTV("两端对齐", "justify")
        ));
    }

    /**
     * 获取字体方向
     * 前端 req_config.js: 获取字体方向 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getFontOrientation")
    public Result<?> getFontOrientation() {
        return Result.ok(Arrays.asList(
                createTV("水平", "horizontal"),
                createTV("垂直", "vertical"),
                createTV("旋转 90 度", "rotate_90"),
                createTV("旋转 -90 度", "rotate_minus_90")
        ));
    }

    /**
     * 获取字体下划线
     * 前端 req_config.js: 获取字体下划线 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getFontUnderline")
    public Result<?> getFontUnderline() {
        return Result.ok(Arrays.asList(
                createTV("无", "none"),
                createTV("单下划线", "single"),
                createTV("双下划线", "double"),
                createTV("着重线", "accounting")
        ));
    }

    /**
     * 获取字体名称
     * 前端 req_config.js: 获取字体名称 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getFontName")
    public Result<?> getFontName() {
        return Result.ok(Arrays.asList(
                createTV("宋体", "SimSun"),
                createTV("微软雅黑", "Microsoft YaHei"),
                createTV("黑体", "SimHei"),
                createTV("楷体", "KaiTi"),
                createTV("Arial", "Arial"),
                createTV("Times New Roman", "Times New Roman"),
                createTV("Courier New", "Courier New"),
                createTV("Verdana", "Verdana"),
                createTV("Tahoma", "Tahoma")
        ));
    }

    /**
     * 获取时区列表
     * 前端 req_config.js: 获取时区 optionsConfig { name: 'text', value: 'value' }
     */
    @PostMapping("/getTimeZone")
    public Result<?> getTimeZone() {
        return Result.ok(Arrays.asList(
                createTV("UTC+8 (北京时间)", "Asia/Shanghai"),
                createTV("UTC+0 (格林威治)", "UTC"),
                createTV("UTC+8 (香港时间)", "Asia/Hong_Kong"),
                createTV("UTC+8 (台北时间)", "Asia/Taipei"),
                createTV("UTC+9 (东京时间)", "Asia/Tokyo"),
                createTV("UTC+1 (欧洲中部)", "Europe/Paris"),
                createTV("UTC-5 (美国东部)", "America/New_York"),
                createTV("UTC-8 (美国西部)", "America/Los_Angeles")
        ));
    }

    /**
     * 获取日期时间格式
     * 前端 req_config.js: 获取时间格式化类型 optionsConfig { name: 'name', value: 'name' }
     * 前端 api/index.ts: getLocalDateTimeFormat()
     */
    @PostMapping("/datetimeformat")
    public Result<?> datetimeformat() {
        return Result.ok(Arrays.asList(
                createNameMap("yyyy-MM-dd HH:mm:ss"),
                createNameMap("yyyy-MM-dd"),
                createNameMap("yyyy/MM/dd"),
                createNameMap("yyyyMMdd"),
                createNameMap("HH:mm:ss"),
                createNameMap("yyyy-MM-dd HH:mm"),
                createNameMap("MM/dd/yyyy"),
                createNameMap("dd/MM/yyyy"),
                createNameMap("yyyy年MM月dd日"),
                createNameMap("yyyy-MM-dd'T'HH:mm:ss'Z'")
        ));
    }

    /**
     * 获取变量类型
     * 前端 req_config.js: 获取变量类型 optionsConfig { name: 'name', value: 'code' }
     */
    @PostMapping("/variableType")
    public Result<?> variableType() {
        return Result.ok(Arrays.asList(
                createMap("name", "系统变量", "code", "system"),
                createMap("name", "环境变量", "code", "environment"),
                createMap("name", "流程变量", "code", "flow"),
                createMap("name", "作业变量", "code", "job"),
                createMap("name", "父作业变量", "code", "parent")
        ));
    }

    /**
     * 获取本地日期类型
     * 前端 req_config.js: 获取本地日期 optionsConfig { name: 'code', value: 'value' }
     */
    @PostMapping("/getLocalDateType")
    public Result<?> getLocalDateType() {
        return Result.ok(Arrays.asList(
                createMap("code", "今天", "value", "today"),
                createMap("code", "昨天", "value", "yesterday"),
                createMap("code", "本周", "value", "this_week"),
                createMap("code", "上周", "value", "last_week"),
                createMap("code", "本月", "value", "this_month"),
                createMap("code", "上月", "value", "last_month"),
                createMap("code", "本年", "value", "this_year"),
                createMap("code", "去年", "value", "last_year")
        ));
    }

    /**
     * 获取值类型元数据
     * 前端 api/index.ts: getValueMeta()
     */
    @PostMapping("/valueMeta")
    public Result<?> valueMeta() {
        // 前端期望带 id 的值类型列表
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createValueMeta(1, "String", "字符串"));
        list.add(createValueMeta(2, "Number", "数值"));
        list.add(createValueMeta(3, "Integer", "整数"));
        list.add(createValueMeta(4, "Date", "日期"));
        list.add(createValueMeta(5, "Boolean", "布尔"));
        list.add(createValueMeta(6, "BigNumber", "大数"));
        list.add(createValueMeta(7, "Binary", "二进制"));
        list.add(createValueMeta(8, "Timestamp", "时间戳"));
        return Result.ok(list);
    }

    /**
     * 获取函数类型
     * 前端 api/index.ts: getfuncType()
     */
    @PostMapping("/func")
    public Result<?> func() {
        return Result.ok(Arrays.asList(
                createNameMap("NONE"),
                createNameMap("SUM"),
                createNameMap("AVERAGE"),
                createNameMap("COUNT"),
                createNameMap("MIN"),
                createNameMap("MAX"),
                createNameMap("CONCAT"),
                createNameMap("SUBSTRING"),
                createNameMap("TRIM"),
                createNameMap("UPPER"),
                createNameMap("LOWER"),
                createNameMap("ROUND"),
                createNameMap("IF"),
                createNameMap("CASE")
        ));
    }

    /**
     * 获取压缩格式
     * 前端 api/index.ts: getCompressionProviderNames()
     */
    @PostMapping("/compressionProviderNames")
    public Result<?> compressionProviderNames() {
        return Result.ok(Arrays.asList(
                createNameMap("None"),
                createNameMap("Zip"),
                createNameMap("Gzip"),
                createNameMap("Bzip2"),
                createNameMap("Snappy"),
                createNameMap("LZ4"),
                createNameMap("LZO")
        ));
    }

    // ========== 工具方法 ==========

    private Map<String, String> createNameMap(String name) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name", name);
        return m;
    }

    private Map<String, String> createTV(String text, String value) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("text", text);
        m.put("value", value);
        return m;
    }

    private Map<String, String> createMap(String k1, String v1, String k2, String v2) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    private Map<String, Object> createValueMeta(int id, String name, String desc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("description", desc);
        return m;
    }
}
