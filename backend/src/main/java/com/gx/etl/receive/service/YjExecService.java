package com.gx.etl.receive.service;

import com.gx.etl.config.DynamicDataSourceManager;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.service.DatabaseSourceService;
import com.gx.etl.receive.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class YjExecService {

    private static final Logger log = LoggerFactory.getLogger(YjExecService.class);

    private final DatabaseSourceService databaseSourceService;
    private final YjHistoryService yjHistoryService;
    private final YjExecLogService yjExecLogService;
    private final YjFileConfigService yjFileConfigService;
    private final YjTableConfigService yjTableConfigService;
    private final YjTableRelationService yjTableRelationService;
    private final YjProtocolService yjProtocolService;

    public YjExecService(DatabaseSourceService databaseSourceService,
                         YjHistoryService yjHistoryService,
                         YjExecLogService yjExecLogService,
                         YjFileConfigService yjFileConfigService,
                         YjTableConfigService yjTableConfigService,
                         YjTableRelationService yjTableRelationService,
                         YjProtocolService yjProtocolService) {
        this.databaseSourceService = databaseSourceService;
        this.yjHistoryService = yjHistoryService;
        this.yjExecLogService = yjExecLogService;
        this.yjFileConfigService = yjFileConfigService;
        this.yjTableConfigService = yjTableConfigService;
        this.yjTableRelationService = yjTableRelationService;
        this.yjProtocolService = yjProtocolService;
    }

    /**
     * 执行数据库表引接（全量覆盖，支持主子表关系）
     */
    public YjExecLog execTable(Long configId, String batch, String createBy) {
        YjTableConfig config = yjTableConfigService.getById(configId);
        if (config == null) {
            throw new RuntimeException("表引接配置不存在: " + configId);
        }

        YjExecLog execLog = new YjExecLog();
        execLog.setBatch(batch);
        execLog.setConfigId(configId);
        execLog.setConfigType("table");
        execLog.setCreateBy(createBy);
        long startTime = System.currentTimeMillis();

        try {
            // 获取源数据源
            DatabaseSource sourceDs = databaseSourceService.getById(config.getDataSourceId());
            if (sourceDs == null) throw new RuntimeException("源数据源不存在，id=" + config.getDataSourceId());
            log.info("引接执行: configId={}, 主表={}, 源数据源={}, 目标数据源={}",
                    configId, config.getTableName(), sourceDs.getName(), config.getTargetDataSourceName());

            // 获取目标数据源
            DatabaseSource targetDs = databaseSourceService.getById(config.getTargetDataSourceId());
            if (targetDs == null) throw new RuntimeException("目标数据源不存在，id=" + config.getTargetDataSourceId());

            // 判断是否有主子表关系配置
            Long tableConfigId = config.getTableConfigId();
            log.info("引接配置 tableConfigId={}", tableConfigId);
            List<Map<String, Object>> tableTree = null;
            if (tableConfigId != null && tableConfigId > 0) {
                YjTableRelation relation = yjTableRelationService.getById(tableConfigId);
                if (relation != null) {
                    log.info("找到主子表关系: configName={}, jsonForm={}", relation.getConfigName(), relation.getJsonForm());
                    if (relation.getJsonForm() != null && !relation.getJsonForm().isEmpty()) {
                        tableTree = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                                relation.getJsonForm(),
                                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                        log.info("加载主子表关系: configName={}, 树节点数={}", relation.getConfigName(), tableTree.size());
                    } else {
                        log.warn("主子表关系 jsonForm 为空，跳过子表处理");
                    }
                } else {
                    log.warn("未找到主子表关系: tableConfigId={}", tableConfigId);
                }
            } else {
                log.info("未配置主子表关系(tableConfigId={})，仅引接主表", tableConfigId);
            }

            int totalRowCount = 0;
            int mainRowCount = 0;
            int childRowCount = 0;

            // 引接主表（全量覆盖）
            mainRowCount = copyTable(sourceDs, config.getSchema(),
                    targetDs, config.getTargetSchema(),
                    config.getTableName(), null, null);
            totalRowCount = mainRowCount;

            log.info("主表 {} 引接完成，复制 {} 条", config.getTableName(), mainRowCount);

            // 如果有主子表关系，遍历子表并按外键关联复制
            if (tableTree != null && !tableTree.isEmpty()) {
                // 收集所有子表节点
                List<Map<String, Object>> childNodes = new ArrayList<>();
                for (Map<String, Object> node : tableTree) {
                    collectChildNodes(node, childNodes);
                }
                log.info("收集到 {} 个子表节点", childNodes.size());

                // 如果树形结构中没有子节点（仅有根节点），将根节点视为子表，
                // 直接按相同字段名从主表复制到该表
                if (childNodes.isEmpty() && tableTree.size() == 1) {
                    Map<String, Object> rootNode = tableTree.get(0);
                    String rootTableName = rootNode.get("tableName") != null ? rootNode.get("tableName").toString() : null;
                    if (rootTableName != null && !rootTableName.isEmpty()
                            && !rootTableName.equalsIgnoreCase(config.getTableName())) {
                        log.info("主子表关系仅配置了根节点 {}，无子节点。自动按相同字段名从主表 {} 复制到 {}",
                                rootTableName, config.getTableName(), rootTableName);
                        int rows = copyTableByMatchingColumns(sourceDs, config.getSchema(),
                                targetDs, config.getTargetSchema(),
                                config.getTableName(), rootTableName);
                        childRowCount += rows;
                        log.info("子表 {} 引接完成，复制 {} 条", rootTableName, rows);
                    }
                }

                // 先从目标库读取主表的主键值
                DataSource tgtDs = getDataSource(targetDs, config.getTargetSchema());
                List<Object> mainKeyValues = new ArrayList<>();
                String mainKeyCol = null;

                if (!childNodes.isEmpty()) {
                    // 取第一个子表定义的 mainKey 作为主表主键
                    Map<String, Object> firstChild = childNodes.get(0);
                    mainKeyCol = firstChild.get("mainKey") != null ? firstChild.get("mainKey").toString() : null;
                    if (mainKeyCol != null && !mainKeyCol.isEmpty()) {
                        String fullTgtTable = buildFullTableName(targetDs.getDbType(), config.getTargetSchema(), config.getTableName());
                        try (Connection conn = tgtDs.getConnection();
                             Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT " + mainKeyCol + " FROM " + fullTgtTable)) {
                            while (rs.next()) {
                                mainKeyValues.add(rs.getObject(1));
                            }
                        }
                        log.info("主表主键列={}, 目标库主表行数={}", mainKeyCol, mainKeyValues.size());
                    }
                }

                // 引接每个子表
                for (Map<String, Object> childNode : childNodes) {
                    String childTableName = childNode.get("tableName") != null ? childNode.get("tableName").toString() : null;
                    String outLinkKey = childNode.get("outLinkKey") != null ? childNode.get("outLinkKey").toString() : null;
                    String childMainKey = childNode.get("mainKey") != null ? childNode.get("mainKey").toString() : null;
                    log.info("子表节点: tableName={}, outLinkKey={}, mainKey={}", childTableName, outLinkKey, childMainKey);

                    if (childTableName == null || childTableName.isEmpty()) continue;

                    int childRows;
                    if (outLinkKey != null && !outLinkKey.isEmpty() && childMainKey != null && !childMainKey.isEmpty()) {
                        // 有外键配置，按外键过滤复制
                        log.info("引接子表: {}, 外键={}, 主表主键={}", childTableName, outLinkKey, childMainKey);
                        childRows = copyTable(sourceDs, config.getSchema(),
                                targetDs, config.getTargetSchema(),
                                childTableName, outLinkKey, mainKeyValues);
                    } else {
                        // 无外键配置，按相同字段名自动复制
                        log.info("引接子表: {}, 按相同字段名自动复制", childTableName);
                        childRows = copyTableByMatchingColumns(sourceDs, config.getSchema(),
                                targetDs, config.getTargetSchema(),
                                config.getTableName(), childTableName);
                    }
                    childRowCount += childRows;
                    log.info("子表 {} 引接完成，复制 {} 条", childTableName, childRows);
                }
            }

            // 记录历史
            YjHistory history = new YjHistory();
            history.setBatch(batch);
            history.setMainBody(tableTree != null ? "数据表引接（含子表）" : "数据表引接");
            history.setConfigId(configId);
            history.setToDataSourceId(config.getTargetDataSourceId());
            history.setToDataSourceName(config.getTargetDataSourceName());
            history.setToSchema(config.getTargetSchema());
            history.setToTable(config.getTableName());
            history.setCreateBy(createBy);
            yjHistoryService.save(history);

            totalRowCount = mainRowCount + childRowCount;
            execLog.setExecStatus(1);
            execLog.setRowCount((long) totalRowCount);
            StringBuilder detail = new StringBuilder();
            detail.append("主表").append(config.getTableName()).append(":").append(mainRowCount).append("条");
            if (childRowCount > 0) {
                detail.append(",子表:").append(childRowCount).append("条");
            }
            execLog.setErrorMsg(detail.toString());
            execLog.setExecTime((int) (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            log.error("引接执行失败: configId={}", configId, e);
            execLog.setExecStatus(2);
            String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " | Cause: " + e.getCause().getMessage();
            }
            execLog.setErrorMsg(errorMsg);
            execLog.setExecTime((int) (System.currentTimeMillis() - startTime));
        }

        yjExecLogService.save(execLog);
        return execLog;
    }

    /**
     * 递归收集子表节点
     */
    private void collectChildNodes(Map<String, Object> node, List<Map<String, Object>> result) {
        Object children = node.get("children");
        if (children instanceof List) {
            for (Object child : (List<?>) children) {
                if (child instanceof Map) {
                    Map<String, Object> childNode = (Map<String, Object>) child;
                    String tableName = childNode.get("tableName") != null ? childNode.get("tableName").toString() : "";
                    if (!tableName.isEmpty()) {
                        result.add(childNode);
                    }
                    collectChildNodes(childNode, result);
                }
            }
        }
    }

    /**
     * 按相同字段名复制主表数据到子表
     */
    private int copyTableByMatchingColumns(DatabaseSource sourceDs, String srcSchema,
                                            DatabaseSource targetDs, String tgtSchema,
                                            String mainTable, String childTable) throws SQLException {
        log.info("copyTableByMatchingColumns: 源表={}.{}, 目标子表={}.{}", srcSchema, mainTable, tgtSchema, childTable);
        DataSource srcDataSource = getDataSource(sourceDs, srcSchema);
        DataSource tgtDataSource = getDataSource(targetDs, tgtSchema);
        String fullSrcMain = buildFullTableName(sourceDs.getDbType(), srcSchema, mainTable);
        String fullTgtChild = buildFullTableName(targetDs.getDbType(), tgtSchema, childTable);

        // 获取主表和子表的列名
        Set<String> mainColumns = getTableColumns(srcDataSource, fullSrcMain);
        Set<String> childColumns = getTableColumns(tgtDataSource, fullTgtChild);

        // 找出相同字段
        List<String> commonColumns = new ArrayList<>();
        for (String col : mainColumns) {
            if (childColumns.contains(col)) {
                commonColumns.add(col);
            }
        }

        if (commonColumns.isEmpty()) {
            log.warn("子表 {} 与主表 {} 无相同字段，跳过", childTable, mainTable);
            return 0;
        }

        log.info("子表 {} 匹配到相同字段: {}", childTable, commonColumns);

        // 从源库主表读取相同字段，写入目标库子表
        String colList = String.join(",", commonColumns);
        String placeholders = String.join(",", Collections.nCopies(commonColumns.size(), "?"));
        String selectSql = "SELECT " + colList + " FROM " + fullSrcMain;
        String insertSql = "INSERT INTO " + fullTgtChild + " (" + colList + ") VALUES (" + placeholders + ")";

        int rowCount = 0;
        try (Connection srcConn = srcDataSource.getConnection();
             Connection tgtConn = tgtDataSource.getConnection()) {

            tgtConn.setAutoCommit(false);
            try {
                // 清空子表
                try (Statement stmt = tgtConn.createStatement()) {
                    stmt.execute("DELETE FROM " + fullTgtChild);
                }

                // 流式复制
                try (Statement srcStmt = srcConn.createStatement();
                     ResultSet rs = srcStmt.executeQuery(selectSql);
                     PreparedStatement tgtPstmt = tgtConn.prepareStatement(insertSql)) {
                    while (rs.next()) {
                        for (int i = 0; i < commonColumns.size(); i++) {
                            tgtPstmt.setObject(i + 1, rs.getObject(commonColumns.get(i)));
                        }
                        tgtPstmt.addBatch();
                        rowCount++;
                        if (rowCount % 1000 == 0) {
                            tgtPstmt.executeBatch();
                        }
                    }
                    tgtPstmt.executeBatch();
                }
                tgtConn.commit();
            } catch (Exception e) {
                tgtConn.rollback();
                throw e;
            }
        }
        return rowCount;
    }

    private Set<String> getTableColumns(DataSource dataSource, String fullTableName) throws SQLException {
        Set<String> columns = new LinkedHashSet<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + fullTableName + " WHERE 1=0")) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columns.add(meta.getColumnName(i).toUpperCase());
            }
        }
        return columns;
    }

    /**
     * 复制单张表数据（全量或按外键过滤）—— 流式读取 + 同事务 DELETE+INSERT
     */
    private int copyTable(DatabaseSource sourceDs, String srcSchema,
                          DatabaseSource targetDs, String tgtSchema,
                          String tableName,
                          String filterColumn, List<Object> filterValues) throws SQLException {
        DataSource srcDataSource = getDataSource(sourceDs, srcSchema);
        List<String> columns = new ArrayList<>();

        // 从源表获取列信息
        try (Connection srcConn = srcDataSource.getConnection()) {
            String fullSrcTable = buildFullTableName(sourceDs.getDbType(), srcSchema, tableName);
            try (Statement stmt = srcConn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM " + fullSrcTable + " WHERE 1=0")) {
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    columns.add(meta.getColumnName(i));
                }
            }
        }

        if (columns.isEmpty()) return 0;

        // 构建查询SQL（带外键过滤）
        String fullSrcTable = buildFullTableName(sourceDs.getDbType(), srcSchema, tableName);
        String querySql;
        if (filterColumn != null && filterValues != null && !filterValues.isEmpty()) {
            StringBuilder sb = new StringBuilder("SELECT * FROM " + fullSrcTable + " WHERE " + filterColumn + " IN (");
            sb.append(filterValues.stream().map(v -> "?").collect(Collectors.joining(",")));
            sb.append(")");
            querySql = sb.toString();
        } else {
            querySql = "SELECT * FROM " + fullSrcTable;
        }

        // 流式读取 + 写入目标表（同事务）
        DataSource tgtDataSource = getDataSource(targetDs, tgtSchema);
        String fullTgtTable = buildFullTableName(targetDs.getDbType(), tgtSchema, tableName);
        int rowCount = 0;

        try (Connection srcConn = srcDataSource.getConnection();
             Connection tgtConn = tgtDataSource.getConnection()) {

            tgtConn.setAutoCommit(false);

            try {
                // 先清空目标表
                try (Statement stmt = tgtConn.createStatement()) {
                    stmt.execute("DELETE FROM " + fullTgtTable);
                }

                // 构造 INSERT SQL
                StringBuilder insertSql = new StringBuilder("INSERT INTO " + fullTgtTable + " (");
                insertSql.append(String.join(",", columns));
                insertSql.append(") VALUES (");
                insertSql.append(String.join(",", columns.stream().map(c -> "?").toArray(String[]::new)));
                insertSql.append(")");

                // 流式：源 ResultSet → 目标 PreparedStatement，逐行处理
                try (PreparedStatement srcPstmt = srcConn.prepareStatement(querySql);
                     PreparedStatement tgtPstmt = tgtConn.prepareStatement(insertSql.toString())) {

                    if (filterColumn != null && filterValues != null && !filterValues.isEmpty()) {
                        for (int i = 0; i < filterValues.size(); i++) {
                            srcPstmt.setObject(i + 1, filterValues.get(i));
                        }
                    }

                    try (ResultSet rs = srcPstmt.executeQuery()) {
                        while (rs.next()) {
                            for (int i = 0; i < columns.size(); i++) {
                                tgtPstmt.setObject(i + 1, rs.getObject(columns.get(i)));
                            }
                            tgtPstmt.addBatch();
                            rowCount++;
                            if (rowCount % 1000 == 0) {
                                tgtPstmt.executeBatch();
                            }
                        }
                        tgtPstmt.executeBatch();
                    }
                }

                tgtConn.commit();
            } catch (Exception e) {
                tgtConn.rollback();
                throw e;
            }
        }

        return rowCount;
    }

    /**
     * 执行文件引接（读文件 → 解析 → 入库，支持解析协议）
     */
    public YjExecLog execFile(Long configId, String batch, String createBy) {
        YjFileConfig config = yjFileConfigService.getById(configId);
        if (config == null) {
            throw new RuntimeException("文件引接配置不存在: " + configId);
        }

        YjExecLog execLog = new YjExecLog();
        execLog.setBatch(batch);
        execLog.setConfigId(configId);
        execLog.setConfigType("file");
        execLog.setCreateBy(createBy);
        long startTime = System.currentTimeMillis();

        try {
            String filePath = config.getFilePath();
            if (filePath == null || filePath.isEmpty()) {
                throw new RuntimeException("文件路径为空");
            }

            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("文件不存在: " + filePath);
            }

            // 获取目标数据源
            DatabaseSource targetDs = databaseSourceService.getById(config.getTargetDataSourceId());
            if (targetDs == null) throw new RuntimeException("目标数据源不存在");

            // 加载解析协议
            YjProtocol protocol = null;
            if (config.getAnalysisProtocolId() != null) {
                protocol = yjProtocolService.getById(config.getAnalysisProtocolId());
            }

            // 确定分隔符
            String separator = "\t"; // 默认制表符
            if (protocol != null && protocol.getSeparator() != null && !protocol.getSeparator().isEmpty()) {
                separator = protocol.getSeparator();
            }

            // 确定目标表名
            String fileName = file.getName();
            String tableName;
            if (protocol != null && protocol.getTableName() != null && !protocol.getTableName().isEmpty()) {
                tableName = protocol.getTableName();
            } else {
                tableName = fileName.substring(0, fileName.lastIndexOf('.'));
                if (tableName.length() > 30) tableName = tableName.substring(0, 30);
            }

            // 解析协议中的列定义
            List<String> columnNames = new ArrayList<>();
            if (protocol != null && protocol.getJsonForm() != null && !protocol.getJsonForm().isEmpty()) {
                List<Map<String, Object>> formColumns = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                        protocol.getJsonForm(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                // 按 columnIndex 排序
                formColumns.sort(Comparator.comparingInt(a -> {
                    Object idx = a.get("columnIndex");
                    return idx instanceof Number ? ((Number) idx).intValue() : Integer.MAX_VALUE;
                }));
                for (Map<String, Object> col : formColumns) {
                    String colName = col.get("columnName") != null ? col.get("columnName").toString() : "";
                    if (!colName.isEmpty()) {
                        columnNames.add(colName);
                    }
                }
            }

            // 判断文件类型并解析
            String fileType = protocol != null ? protocol.getFileType() : null;
            List<String[]> fileRows;
            if ("XML".equalsIgnoreCase(fileType)) {
                fileRows = parseXmlFile(file);
            } else {
                fileRows = parseTextFile(file, separator, config.getJumpRow());
            }

            if (fileRows.isEmpty()) {
                throw new RuntimeException("文件内容为空");
            }

            // 写入目标库
            DataSource tgtDataSource = getDataSource(targetDs, config.getTargetSchema());
            String tgtSchema = config.getTargetSchema();
            String fullTgtTable = buildFullTableName(targetDs.getDbType(), tgtSchema, tableName);
            int colCount = columnNames.isEmpty() ? fileRows.get(0).length : columnNames.size();

            int rowCount = 0;
            try (Connection tgtConn = tgtDataSource.getConnection()) {
                tgtConn.setAutoCommit(false);
                try {
                    // 检查表是否存在，不存在则创建
                    boolean tableExists = checkTableExists(tgtConn, targetDs.getDbType(), tgtSchema, tableName);
                    if (!tableExists) {
                        StringBuilder createSql = new StringBuilder("CREATE TABLE " + fullTgtTable + " (");
                        if (protocol != null && !columnNames.isEmpty()) {
                            for (int i = 0; i < columnNames.size(); i++) {
                                if (i > 0) createSql.append(", ");
                                createSql.append(columnNames.get(i) + " VARCHAR(2000)");
                            }
                        } else {
                            for (int i = 0; i < colCount; i++) {
                                if (i > 0) createSql.append(", ");
                                createSql.append("COL_" + (i + 1) + " VARCHAR(2000)");
                            }
                        }
                        createSql.append(")");
                        try (Statement stmt = tgtConn.createStatement()) {
                            stmt.execute(createSql.toString());
                        }
                    } else {
                        try (Statement stmt = tgtConn.createStatement()) {
                            stmt.execute("DELETE FROM " + fullTgtTable);
                        }
                    }

                    // 批量插入
                    StringBuilder insertSql = new StringBuilder("INSERT INTO " + fullTgtTable + " VALUES (");
                    insertSql.append(String.join(",", java.util.Collections.nCopies(colCount, "?")));
                    insertSql.append(")");

                    try (PreparedStatement pstmt = tgtConn.prepareStatement(insertSql.toString())) {
                        for (String[] row : fileRows) {
                            for (int i = 0; i < colCount && i < row.length; i++) {
                                pstmt.setString(i + 1, row[i]);
                            }
                            pstmt.addBatch();
                            rowCount++;
                            if (rowCount % 1000 == 0) {
                                pstmt.executeBatch();
                            }
                        }
                        pstmt.executeBatch();
                    }
                    tgtConn.commit();
                } catch (Exception e) {
                    tgtConn.rollback();
                    throw e;
                }
            }

            // 记录历史
            YjHistory history = new YjHistory();
            history.setBatch(batch);
            history.setMainBody("文件引接");
            history.setConfigId(configId);
            history.setToDataSourceId(config.getTargetDataSourceId());
            history.setToDataSourceName(config.getTargetDataSourceName());
            history.setToSchema(config.getTargetSchema());
            history.setToTable(tableName);
            history.setCreateBy(createBy);
            yjHistoryService.save(history);

            execLog.setExecStatus(1);
            execLog.setRowCount((long) rowCount);
            execLog.setExecTime((int) (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            execLog.setExecStatus(2);
            execLog.setErrorMsg(e.getMessage());
            execLog.setExecTime((int) (System.currentTimeMillis() - startTime));
        }

        yjExecLogService.save(execLog);
        return execLog;
    }

    /**
     * 解析文本文件（TXT/CSV/DAT），按分隔符拆分
     */
    private List<String[]> parseTextFile(File file, String separator, String jumpRow) throws IOException {
        List<String[]> rows = new ArrayList<>();
        List<Integer> jumpRows = new ArrayList<>();
        if (jumpRow != null && !jumpRow.isEmpty()) {
            for (String s : jumpRow.split(",")) {
                jumpRows.add(Integer.parseInt(s.trim()));
            }
        }
        // Java regex 需要转义特殊字符
        String regex = "\t".equals(separator) ? "\t" : java.util.regex.Pattern.quote(separator);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (jumpRows.contains(lineNum)) continue;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(regex, -1);
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                rows.add(parts);
            }
        }
        return rows;
    }

    /**
     * 解析扁平 XML 文件，<record> 下 <key>value</key> 拍平成行
     */
    private List<String[]> parseXmlFile(File file) throws Exception {
        List<String[]> rows = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();

        // 找到所有 record 节点
        NodeList recordNodes = doc.getElementsByTagName("record");
        for (int i = 0; i < recordNodes.getLength(); i++) {
            Element record = (Element) recordNodes.item(i);
            NodeList fields = record.getChildNodes();
            List<String> values = new ArrayList<>();
            List<String> colNames = new ArrayList<>();
            for (int j = 0; j < fields.getLength(); j++) {
                if (fields.item(j) instanceof Element) {
                    Element field = (Element) fields.item(j);
                    colNames.add(field.getTagName());
                    values.add(field.getTextContent().trim());
                }
            }
            if (i == 0 && !colNames.isEmpty()) {
                rows.add(colNames.toArray(new String[0]));
            }
            if (!values.isEmpty()) {
                rows.add(values.toArray(new String[0]));
            }
        }
        return rows;
    }

    /**
     * 生成批次号
     */
    public String generateBatchNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PC" + timestamp + "_" + uuid;
    }

    private DataSource getDataSource(DatabaseSource ds, String schema) {
        return DynamicDataSourceManager.getInstance().getDataSource(
                ds.getId(), ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid(), schema);
    }

    private String buildFullTableName(String dbType, String schema, String tableName) {
        if ("MYSQL".equalsIgnoreCase(dbType)) {
            return tableName;
        }
        if (schema != null && !schema.isEmpty()) {
            return schema + "." + tableName;
        }
        return tableName;
    }

    private boolean checkTableExists(Connection conn, String dbType, String schema, String tableName) throws SQLException {
        try {
            String sql = "SELECT * FROM " + buildFullTableName(dbType, schema, tableName) + " WHERE 1=0";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
