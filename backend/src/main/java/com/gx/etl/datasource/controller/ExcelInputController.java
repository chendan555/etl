package com.gx.etl.datasource.controller;

import com.alibaba.excel.EasyExcel;
import com.gx.etl.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * Excel输入算子配置所需的API端点。
 * 为前端 ExcelInput 组件的下拉选项、字段获取、文件上传/预览提供数据。
 */
@RestController
@RequestMapping("/api/excelInputController")
public class ExcelInputController {

    @Value("${file.upload.dir:d:/project/ai/vs/03/backend/uploads}")
    private String uploadDir;

    /**
     * 获取表格类型列表
     * 前端 req_config.js: 表格类型 optionsConfig { name: 'description', value: 'description' }
     */
    @PostMapping("/getSpreadSheetType")
    public Result<?> getSpreadSheetType() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(createMap("description", "Excel 97-2003 工作簿 (*.xls)"));
        list.add(createMap("description", "Excel 工作簿 (*.xlsx)"));
        list.add(createMap("description", "CSV 文件 (*.csv)"));
        list.add(createMap("description", "OpenDocument 表格 (*.ods)"));
        return Result.ok(list);
    }

    /**
     * 获取字段格式化类型
     * 前端 req_config.js: 格式化类型 optionsConfig { name: 'name', value: 'name' }
     * res_options_deal.js: getFieldFormats 过滤 { name, formate[] } → 按 valueType 匹配返回格式化列表
     */
    @PostMapping("/getFieldFormats")
    public Result<?> getFieldFormats(@RequestParam(required = false) String valueType) {
        List<Map<String, Object>> list = new ArrayList<>();

        // String 类型格式
        list.add(createFieldFormat("String", Arrays.asList(
                "none", "trim", "upper", "lower", "capitalize"
        )));
        // Number 类型格式
        list.add(createFieldFormat("Number", Arrays.asList(
                "#,##0.00", "#,##0", "0.00", "0", "percent"
        )));
        // Date 类型格式
        list.add(createFieldFormat("Date", Arrays.asList(
                "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd",
                "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
                "MM/dd/yyyy", "dd/MM/yyyy"
        )));
        // Boolean 类型格式
        list.add(createFieldFormat("Boolean", Arrays.asList(
                "true/false", "Y/N", "1/0", "yes/no"
        )));

        return Result.ok(list);
    }

    /**
     * 获取去除空格类型列表
     * 前端 req_config.js: 去除空格类型 optionsConfig { name: 'desc', value: 'code' }
     */
    @PostMapping("/getTrimType")
    public Result<?> getTrimType() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(createMap("desc", "不去掉空格", "code", "none"));
        list.add(createMap("desc", "去掉左空格", "code", "left"));
        list.add(createMap("desc", "去掉右空格", "code", "right"));
        list.add(createMap("desc", "去掉左右两端空格", "code", "both"));
        return Result.ok(list);
    }

    /**
     * 获取 Excel 工作表列表
     * 前端 res_deal.js: getExcelSheet 期望 res.data 为 [{ name: "Sheet1" }, ...]
     */
    @PostMapping("/getExcelSheet")
    public Result<?> getExcelSheet(@RequestParam(required = false) String fileName,
                                    @RequestParam(required = false) String filefield,
                                    @RequestParam(required = false) String filePath) {
        try {
            String path = extractPath(fileName);
            if (path == null || path.isEmpty()) path = extractPath(filefield);
            if (path == null || path.isEmpty()) path = extractPath(filePath);
            System.out.println("[getExcelSheet] fileName=" + fileName + ", filefield=" + filefield + ", filePath=" + filePath + " → path=" + path);
            if (path == null || path.isEmpty()) {
                return Result.fail("文件路径不能为空");
            }
            File file = new File(path);
            if (!file.exists()) {
                return Result.fail("文件不存在: " + path);
            }

            // 使用 EasyExcel 读取 sheet 名称
            List<Map<String, Object>> sheets = new ArrayList<>();
            com.alibaba.excel.ExcelReader excelReader = EasyExcel.read(file).build();
            List<com.alibaba.excel.read.metadata.ReadSheet> sheetList = excelReader.excelExecutor().sheetList();
            if (sheetList != null) {
                for (com.alibaba.excel.read.metadata.ReadSheet sheet : sheetList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", sheet.getSheetName());
                    item.put("index", sheet.getSheetNo() != null ? sheet.getSheetNo() : 0);
                    sheets.add(item);
                }
            }
            excelReader.finish();

            return Result.ok(sheets);
        } catch (Exception e) {
            return Result.fail("读取Excel工作表失败: " + e.getMessage());
        }
    }

    /**
     * 获取 Excel 字段列表（读取表头行）
     * 前端 res_deal.js: getExcelFields 期望 res.data 为 [{ name: "columnName" }, ...]
     */
    @PostMapping("/getFields")
    public Result<?> getFields(@RequestParam(required = false) String fileName,
                                @RequestParam(required = false) String filefield,
                                @RequestParam(required = false) String filePath,
                                @RequestParam(required = false) Integer index,
                                @RequestParam(required = false) Integer sheetIndex,
                                @RequestParam(required = false) Integer sheetNo) {
        try {
            String path = extractPath(fileName);
            if (path == null || path.isEmpty()) path = extractPath(filefield);
            if (path == null || path.isEmpty()) path = extractPath(filePath);
            if (sheetIndex == null) sheetIndex = index;
            if (sheetIndex == null) sheetIndex = sheetNo;
            if (sheetIndex == null) sheetIndex = 0;

            if (path == null || path.isEmpty()) {
                return Result.fail("文件路径不能为空");
            }
            File file = new File(path);
            if (!file.exists()) {
                return Result.fail("文件不存在: " + path);
            }

            List<Map<String, Object>> fields = new ArrayList<>();

            // 读取第一行作为表头
            EasyExcel.read(path, new com.alibaba.excel.event.AnalysisEventListener<Map<Integer, String>>() {
                boolean headerRead = false;

                @Override
                public void invoke(Map<Integer, String> data, com.alibaba.excel.context.AnalysisContext context) {
                    if (!headerRead) {
                        for (int i = 0; i < data.size(); i++) {
                            String colName = data.get(i);
                            if (colName == null || colName.trim().isEmpty()) {
                                colName = "field" + (i + 1);
                            }
                            Map<String, Object> item = new HashMap<>();
                            item.put("name", colName.trim());
                            fields.add(item);
                        }
                        headerRead = true;
                    }
                }

                @Override
                public void doAfterAllAnalysed(com.alibaba.excel.context.AnalysisContext context) {}
            }).sheet(sheetIndex).headRowNumber(1).doRead();

            return Result.ok(fields);
        } catch (Exception e) {
            return Result.fail("读取Excel字段失败: " + e.getMessage());
        }
    }

    /**
     * 上传 Excel 文件
     */
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        try {
            if (uploadFile.isEmpty()) {
                return Result.fail("请选择文件");
            }

            // 确保上传目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalName = uploadFile.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;

            // 保存文件
            File dest = new File(dir, fileName);
            uploadFile.transferTo(dest);

            Map<String, Object> result = new HashMap<>();
            result.put("path", dest.getAbsolutePath());
            result.put("name", originalName);
            result.put("size", uploadFile.getSize());

            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }

    /**
     * 预览 Excel 数据
     */
    @PostMapping("/excelPreview")
    public Result<?> excelPreview(@RequestParam(required = false) String fileName,
                                   @RequestParam(required = false) String filefield,
                                   @RequestParam(required = false) String filePath,
                                   @RequestParam(required = false) Integer index,
                                   @RequestParam(required = false) Integer sheetIndex,
                                   @RequestParam(required = false) Integer sheetNo,
                                   @RequestParam(required = false) Integer limit,
                                   @RequestParam(required = false) Integer previewRows) {
        try {
            String path = extractPath(fileName);
            if (path == null || path.isEmpty()) path = extractPath(filefield);
            if (path == null || path.isEmpty()) path = extractPath(filePath);
            Integer si = sheetIndex;
            if (si == null) si = index;
            if (si == null) si = sheetNo;
            if (si == null) si = 0;
            int previewLimit = limit != null ? limit : (previewRows != null ? previewRows : 100);
            if (previewLimit <= 0) previewLimit = 100;
            final int effectiveLimit = previewLimit;

            if (path == null || path.isEmpty()) {
                return Result.fail("文件路径不能为空");
            }
            File file = new File(path);
            if (!file.exists()) {
                return Result.fail("文件不存在: " + path);
            }

            List<String> columns = new ArrayList<>();
            List<Map<String, Object>> rows = new ArrayList<>();
            boolean[] headerRead = {false};
            int[] rowCount = {0};

            EasyExcel.read(path, new com.alibaba.excel.event.AnalysisEventListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> data, com.alibaba.excel.context.AnalysisContext context) {
                    if (!headerRead[0]) {
                        for (int i = 0; i < data.size(); i++) {
                            columns.add(data.get(i) != null ? data.get(i) : "col" + (i + 1));
                        }
                        headerRead[0] = true;
                        return;
                    }

                    if (rowCount[0] >= effectiveLimit) return;

                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 0; i < columns.size(); i++) {
                        row.put(columns.get(i), data.getOrDefault(i, ""));
                    }
                    rows.add(row);
                    rowCount[0]++;
                }

                @Override
                public void doAfterAllAnalysed(com.alibaba.excel.context.AnalysisContext context) {}
            }).sheet(si).doRead();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("columns", columns);
            result.put("rows", rows);
            result.put("total", rowCount[0]);

            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail("预览失败: " + e.getMessage());
        }
    }

    // ========== 工具方法 ==========

    private Map<String, String> createMap(String k1, String v1) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(k1, v1);
        return m;
    }

    private Map<String, String> createMap(String k1, String v1, String k2, String v2) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    private Map<String, Object> createFieldFormat(String name, List<String> formats) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("formate", formats);
        return m;
    }

    /**
     * 提取文件路径：兼容前端传递的 JSON 数组格式 "[\"path\"]" 和纯字符串
     */
    private String extractPath(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        String trimmed = raw.trim();
        // 如果是以 [ 开头的 JSON 数组，提取第一个元素
        if (trimmed.startsWith("[")) {
            try {
                // 去掉首尾的 []，再去除引号
                String inner = trimmed.substring(1, trimmed.length() - 1).trim();
                if (inner.startsWith("\"") && inner.endsWith("\"") && inner.length() >= 2) {
                    return inner.substring(1, inner.length() - 1);
                }
                return inner.isEmpty() ? null : inner;
            } catch (Exception e) {
                return trimmed;
            }
        }
        return trimmed;
    }
}