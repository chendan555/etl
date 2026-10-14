package com.gx.etl.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages dynamic database connections for external data sources.
 * Each data source gets its own HikariCP connection pool.
 */
public class DynamicDataSourceManager {

    private static final DynamicDataSourceManager INSTANCE = new DynamicDataSourceManager();
    private final Map<Long, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();
    /** 数据源 id -> 当前连接配置指纹，配置未变时直接复用池 */
    private final Map<Long, String> dataSourceFingerprints = new ConcurrentHashMap<>();
    /** 数据源 id -> 锁对象，避免多线程并发重建池 */
    private final Map<Long, Object> dataSourceLocks = new ConcurrentHashMap<>();

    private DynamicDataSourceManager() {}

    public static DynamicDataSourceManager getInstance() {
        return INSTANCE;
    }

    public DataSource getDataSource(Long id, String dbType, String host, int port,
                                     String databaseName, String username, String password,
                                     String sid) {
        return getDataSource(id, dbType, host, port, databaseName, username, password, sid, null);
    }

    /**
     * 获取或创建 DataSource，支持 MySQL 用 schema 覆盖 databaseName。
     * 配置未变化时复用已有连接池，避免批量多线程下 close 掉在用连接。
     */
    public DataSource getDataSource(Long id, String dbType, String host, int port,
                                     String databaseName, String username, String password,
                                     String sid, String schema) {
        // MySQL: 如果 databaseName 为空且有 schema，用 schema 作为库名
        final String actualDbName;
        if ("MYSQL".equalsIgnoreCase(dbType) && (databaseName == null || databaseName.isEmpty()) && schema != null && !schema.isEmpty()) {
            actualDbName = schema;
        } else {
            actualDbName = databaseName;
        }

        final String fingerprint = buildFingerprint(dbType, host, port, actualDbName, username, password, sid);

        HikariDataSource existing = dataSourceMap.get(id);
        if (existing != null && !existing.isClosed()
                && fingerprint.equals(dataSourceFingerprints.get(id))) {
            return existing;
        }

        Object lock = dataSourceLocks.computeIfAbsent(id, k -> new Object());
        synchronized (lock) {
            existing = dataSourceMap.get(id);
            if (existing != null && !existing.isClosed()
                    && fingerprint.equals(dataSourceFingerprints.get(id))) {
                return existing;
            }

            HikariDataSource created = createDataSource(id, dbType, host, port, actualDbName, username, password, sid);
            HikariDataSource old = dataSourceMap.put(id, created);
            dataSourceFingerprints.put(id, fingerprint);
            // 旧池延迟关闭：给已借出的连接留出归还时间，避免批量执行时「连接已关闭」
            closeQuietlyLater(old);
            return created;
        }
    }

    private static String buildFingerprint(String dbType, String host, int port,
                                           String dbName, String username, String password, String sid) {
        return String.join("|",
                String.valueOf(dbType),
                String.valueOf(host),
                String.valueOf(port),
                String.valueOf(dbName),
                String.valueOf(username),
                String.valueOf(password),
                String.valueOf(sid));
    }

    private void closeQuietlyLater(HikariDataSource ds) {
        if (ds == null) {
            return;
        }
        Thread t = new Thread(() -> {
            try {
                // 等待在途业务释放连接后再真正关闭
                Thread.sleep(30_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                ds.close();
            } catch (Exception ignored) {
            }
        }, "hikari-ds-soft-close");
        t.setDaemon(true);
        t.start();
    }

    private HikariDataSource createDataSource(Long id, String dbType, String host, int port,
                                               String databaseName, String username, String password,
                                               String sid) {
        // 对于 DM 数据库，如果 databaseName 不是有效的 schema 名称，则不使用
        String actualDbName = databaseName;
        if ("DM".equalsIgnoreCase(dbType) && !isValidSchemaName(databaseName)) {
            actualDbName = null;
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildJdbcUrl(dbType, host, port, actualDbName, sid));
        config.setUsername(username);
        config.setPassword(password);
        // 批量并行时表输入/表输出会同时借连接，池给大一些
        // （流程批处理4线程×2连接 + 引接6线程×1连接，同一数据源峰值可到14，取20留余量）
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);
        config.setDriverClassName(getDriverClass(dbType));
        config.setPoolName("dynamic-ds-" + id + "-" + host + "-" + port);
        return new HikariDataSource(config);
    }

    public void removeDataSource(Long id) {
        HikariDataSource ds = dataSourceMap.remove(id);
        dataSourceFingerprints.remove(id);
        dataSourceLocks.remove(id);
        if (ds != null) {
            closeQuietlyLater(ds);
        }
    }

    public boolean testConnection(String dbType, String host, int port,
                                   String databaseName, String username, String password,
                                   String sid) {
        // 对于 DM 数据库，如果 databaseName 不是有效的 schema 名称（如包含中文），则不使用
        String actualDbName = databaseName;
        if ("DM".equalsIgnoreCase(dbType) && !isValidSchemaName(databaseName)) {
            actualDbName = null;
        }
        String url = buildJdbcUrl(dbType, host, port, actualDbName, sid);
        try (Connection conn = java.sql.DriverManager.getConnection(url, username, password)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            throw new RuntimeException("连接失败: " + e.getMessage(), e);
        }
    }

    public List<String> getTables(Long id, String dbType, String host, int port,
                                   String databaseName, String username, String password,
                                   String sid) throws SQLException {
        return getTables(id, dbType, host, port, databaseName, username, password, sid, null);
    }

    public List<String> getTables(Long id, String dbType, String host, int port,
                                   String databaseName, String username, String password,
                                   String sid, String schema) throws SQLException {
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid, schema);
        List<String> tables = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            // MySQL: catalog 参数才是数据库名，schema 参数在 MySQL 中不用
            if ("MYSQL".equalsIgnoreCase(dbType)) {
                String catalog = conn.getCatalog();
                try (ResultSet rs = meta.getTables(catalog, null, "%", new String[]{"TABLE", "VIEW"})) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
            } else {
                String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
                try (ResultSet rs = meta.getTables(null, actualSchema, "%", new String[]{"TABLE", "VIEW"})) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
            }
        }
        return tables;
    }

    public List<Map<String, Object>> getColumns(Long id, String dbType, String host, int port,
                                                  String databaseName, String username, String password,
                                                  String sid, String tableName) throws SQLException {
        return getColumns(id, dbType, host, port, databaseName, username, password, sid, tableName, null);
    }

    public List<Map<String, Object>> getColumns(Long id, String dbType, String host, int port,
                                                  String databaseName, String username, String password,
                                                  String sid, String tableName, String schema) throws SQLException {
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid, schema);
        List<Map<String, Object>> columns = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            // DM数据库 meta.getColumns 可能因 SF_GET_OCT_LENGTH 报错，先尝试用 ResultSetMetaData
            if ("DM".equalsIgnoreCase(dbType)) {
                String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
                String fullTable = buildFullTableName(actualSchema, tableName, dbType);
                String sql = "SELECT * FROM " + fullTable + " WHERE 1=0";
                System.out.println("[getColumns] SQL: " + sql);
                try (java.sql.Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    java.sql.ResultSetMetaData rsMeta = rs.getMetaData();
                    for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                        Map<String, Object> col = new LinkedHashMap<>();
                        col.put("name", rsMeta.getColumnName(i));
                        col.put("comments", "");
                        col.put("type", rsMeta.getColumnTypeName(i));
                        col.put("lenght", rsMeta.getPrecision(i));
                        columns.add(col);
                    }
                }
                return columns;
            }

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = null;
            try {
                // MySQL: catalog 参数才是数据库名
                if ("MYSQL".equalsIgnoreCase(dbType)) {
                    String catalog = conn.getCatalog();
                    rs = meta.getColumns(catalog, null, tableName, "%");
                } else {
                    String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
                    rs = meta.getColumns(null, actualSchema, tableName, "%");
                }
                while (rs.next()) {
                    Map<String, Object> col = new LinkedHashMap<>();
                    col.put("name", rs.getString("COLUMN_NAME"));
                    col.put("comments", rs.getString("REMARKS"));
                    col.put("type", rs.getString("TYPE_NAME"));
                    col.put("lenght", rs.getInt("COLUMN_SIZE"));
                    columns.add(col);
                }
            } finally {
                if (rs != null) rs.close();
            }
        }
        return columns;
    }

    private String buildJdbcUrl(String dbType, String host, int port, String dbName, String sid) {
        switch (dbType.toUpperCase()) {
            case "DM":
                // DM 数据库：dbName 为空时使用默认 schema
                if (dbName == null || dbName.isEmpty()) {
                    return String.format("jdbc:dm://%s:%s", host, port);
                }
                return String.format("jdbc:dm://%s:%s/%s", host, port, dbName);
            case "ORACLE":
                if (sid != null && !sid.isEmpty()) {
                    return String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, sid);
                }
                return String.format("jdbc:oracle:thin:@//%s:%s/%s", host, port, dbName);
            case "MYSQL":
                if (dbName == null || dbName.isEmpty()) {
                    // 允许不指定库名，连接后探测默认库
                    return String.format("jdbc:mysql://%s:%s/?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true", host, port);
                }
                return String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true", host, port, dbName);
            case "POSTGRESQL":
                return String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
            case "KINGBASE":
                return String.format("jdbc:kingbase8://%s:%s/%s", host, port, dbName);
            default:
                throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
        }
    }

    /**
     * 判断是否为有效的 schema 名称（非中文、非纯数字等）
     */
    private boolean isValidSchemaName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        // 如果包含中文字符，则认为是显示名称而非 schema
        for (char c : name.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return false;
            }
        }
        return true;
    }

    /**
     * 构建完整的表名引用，处理表名已包含 schema 前缀的情况，并对 DM/Oracle 加引号
     */
    private String buildFullTableName(String schema, String tableName, String dbType) {
        // 如果表名已包含 schema 前缀（含点号），直接使用，避免重复拼接
        if (tableName != null && tableName.contains(".")) {
            return tableName;
        }
        if (schema == null || schema.isEmpty()) {
            return tableName;
        }
        return schema + "." + tableName;
    }

    private String getDriverClass(String dbType) {
        switch (dbType.toUpperCase()) {
            case "DM": return "dm.jdbc.driver.DmDriver";
            case "ORACLE": return "oracle.jdbc.OracleDriver";
            case "MYSQL": return "com.mysql.cj.jdbc.Driver";
            case "POSTGRESQL": return "org.postgresql.Driver";
            case "KINGBASE": return "com.kingbase8.Driver";
            default: throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
        }
    }

    private String getSchema(String dbType, String dbName, String username) {
        switch (dbType.toUpperCase()) {
            case "DM": return username != null ? username.toUpperCase() : "SYSDBA";
            case "POSTGRESQL": return "public";
            case "ORACLE": return username != null ? username.toUpperCase() : null;
            default: return null;
        }
    }

    /**
     * 获取数据库的所有 schema 列表
     */
    public List<String> getSchemas(String dbType, String host, int port,
                                    String databaseName, String username, String password,
                                    String sid) throws SQLException {
        List<String> schemas = new ArrayList<>();

        // MySQL: databaseName 为空时，用无 database 的 URL 连接获取所有库列表
        if ("MYSQL".equalsIgnoreCase(dbType) && (databaseName == null || databaseName.isEmpty())) {
            String url = String.format("jdbc:mysql://%s:%s/?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true", host, port);
            try (Connection conn = java.sql.DriverManager.getConnection(url, username, password)) {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet rs = meta.getCatalogs()) {
                    while (rs.next()) {
                        schemas.add(rs.getString("TABLE_CAT"));
                    }
                }
            }
            return schemas;
        }

        String url = buildJdbcUrl(dbType, host, port, databaseName, sid);

        try (Connection conn = java.sql.DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData meta = conn.getMetaData();

            switch (dbType.toUpperCase()) {
                case "DM":
                case "ORACLE":
                    // DM 和 Oracle 使用 getSchemas
                    try (ResultSet rs = meta.getSchemas()) {
                        while (rs.next()) {
                            String schema = rs.getString("TABLE_SCHEM");
                            if (schema != null && !schema.startsWith("SYS") && !schema.startsWith("CTISYS")) {
                                schemas.add(schema);
                            }
                        }
                    }
                    break;
                case "MYSQL":
                    // MySQL 的 schema 就是 database
                    try (ResultSet rs = meta.getCatalogs()) {
                        while (rs.next()) {
                            schemas.add(rs.getString("TABLE_CAT"));
                        }
                    }
                    break;
                case "POSTGRESQL":
                case "KINGBASE":
                    try (ResultSet rs = meta.getSchemas()) {
                        while (rs.next()) {
                            String schema = rs.getString("TABLE_SCHEM");
                            if (schema != null && !schema.startsWith("pg_") && !schema.equals("information_schema")) {
                                schemas.add(schema);
                            }
                        }
                    }
                    break;
            }
        }

        // 如果没有获取到 schema，返回默认值
        if (schemas.isEmpty()) {
            if ("DM".equalsIgnoreCase(dbType) || "ORACLE".equalsIgnoreCase(dbType)) {
                schemas.add(username != null ? username.toUpperCase() : "SYSDBA");
            } else {
                schemas.add("public");
            }
        }

        return schemas;
    }

    /**
     * 获取表数据（分页）
     */
    public Map<String, Object> getTableData(Long id, String dbType, String host, int port,
                                              String databaseName, String username, String password,
                                              String sid, String tableName, int pageIndex, int pageSize) throws SQLException {
        return getTableData(id, dbType, host, port, databaseName, username, password, sid, tableName, pageIndex, pageSize, null);
    }

    public Map<String, Object> getTableData(Long id, String dbType, String host, int port,
                                              String databaseName, String username, String password,
                                              String sid, String tableName, int pageIndex, int pageSize, String schema) throws SQLException {
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid, schema);
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> columnList = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
        // MySQL: 连接已指定库名，表名不加 schema 前缀
        String fullTableName;
        if ("MYSQL".equalsIgnoreCase(dbType)) {
            fullTableName = tableName;
        } else {
            fullTableName = buildFullTableName(actualSchema, tableName, dbType);
        }

        System.out.println("[getTableData] dbType=" + dbType + ", databaseName=" + databaseName + ", schema=" + schema + ", fullTableName=" + fullTableName);
        // 先获取列信息（含注释）
        try (Connection conn = ds.getConnection()) {
            System.out.println("[getTableData] 连接URL: " + conn.getMetaData().getURL());
            DatabaseMetaData meta = conn.getMetaData();
            // MySQL: catalog 参数才是数据库名
            ResultSet colRs = null;
            try {
                if ("MYSQL".equalsIgnoreCase(dbType)) {
                    colRs = meta.getColumns(conn.getCatalog(), null, tableName, "%");
                } else {
                    colRs = meta.getColumns(null, actualSchema, tableName, "%");
                }
                while (colRs.next()) {
                    Map<String, Object> col = new LinkedHashMap<>();
                    col.put("columnName", colRs.getString("COLUMN_NAME"));
                    col.put("comments", colRs.getString("REMARKS"));
                    columnList.add(col);
                }
            } finally {
                if (colRs != null) colRs.close();
            }

            // 如果 meta.getColumns 没返回（DM 对非默认 schema 的兼容问题），用查询获取
            String sql = "SELECT * FROM " + fullTableName + " WHERE 1=0";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                java.sql.ResultSetMetaData rsMeta = rs.getMetaData();
                if (columnList.isEmpty()) {
                    for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                        Map<String, Object> col = new LinkedHashMap<>();
                        col.put("columnName", rsMeta.getColumnName(i));
                        col.put("comments", "");
                        columnList.add(col);
                    }
                }
                for (Map<String, Object> col : columnList) {
                    columnNames.add((String) col.get("columnName"));
                }
            }

            // 获取数据（分页）
            String dataSql = "SELECT * FROM " + fullTableName;
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(dataSql);
                 ResultSet rs = stmt.executeQuery()) {
                int start = (pageIndex - 1) * pageSize;
                int totalCount = 0;
                int count = 0;
                while (rs.next()) {
                    totalCount++;
                    if (count < start) {
                        count++;
                        continue;
                    }
                    if (count >= start + pageSize) {
                        count++;
                        continue;
                    }
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnNames) {
                        row.put(col, rs.getObject(col));
                    }
                    rows.add(row);
                    count++;
                }

                // 如果没有更多行，totalCount 就是 count
                if (totalCount == 0 || totalCount <= start + pageSize) {
                    // totalCount 已经准确
                } else {
                    // 还有更多行，需要查询总数
                    // 简单处理：用当前已知的 totalCount
                }

                Map<String, Object> page = new HashMap<>();
                page.put("total", totalCount);
                page.put("records", rows);

                Map<String, Object> result = new HashMap<>();
                result.put("column", columnList);
                result.put("page", page);
                return result;
            }
        }
    }

    /**
     * 获取列值列表
     */
    public List<Object> getColumnValues(Long id, String dbType, String host, int port,
                                          String databaseName, String username, String password,
                                          String sid, String tableName, String columnName) throws SQLException {
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid, null);
        List<Object> values = new ArrayList<>();

        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT DISTINCT " + columnName + " FROM " + tableName + " WHERE " + columnName + " IS NOT NULL";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    values.add(rs.getObject(1));
                }
            }
        }

        return values;
    }
}
