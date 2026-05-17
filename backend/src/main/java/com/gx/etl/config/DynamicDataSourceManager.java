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

    private DynamicDataSourceManager() {}

    public static DynamicDataSourceManager getInstance() {
        return INSTANCE;
    }

    public DataSource getDataSource(Long id, String dbType, String host, int port,
                                     String databaseName, String username, String password,
                                     String sid) {
        return dataSourceMap.computeIfAbsent(id, k -> {
            // 对于 DM 数据库，如果 databaseName 不是有效的 schema 名称，则不使用
            String actualDbName = databaseName;
            if ("DM".equalsIgnoreCase(dbType) && !isValidSchemaName(databaseName)) {
                actualDbName = null;
            }
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(buildJdbcUrl(dbType, host, port, actualDbName, sid));
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setIdleTimeout(60000);
            config.setMaxLifetime(300000);
            config.setConnectionTimeout(10000);
            config.setDriverClassName(getDriverClass(dbType));
            return new HikariDataSource(config);
        });
    }

    public void removeDataSource(Long id) {
        HikariDataSource ds = dataSourceMap.remove(id);
        if (ds != null) {
            ds.close();
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
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid);
        List<String> tables = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
            try (ResultSet rs = meta.getTables(null, actualSchema, "%", new String[]{"TABLE", "VIEW"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
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
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid);
        List<Map<String, Object>> columns = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
            try (ResultSet rs = meta.getColumns(null, actualSchema, tableName, "%")) {
                while (rs.next()) {
                    Map<String, Object> col = new LinkedHashMap<>();
                    col.put("name", rs.getString("COLUMN_NAME"));
                    col.put("comments", rs.getString("REMARKS"));
                    col.put("type", rs.getString("TYPE_NAME"));
                    col.put("lenght", rs.getInt("COLUMN_SIZE"));
                    columns.add(col);
                }
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
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid);
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> columnList = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        String actualSchema = (schema != null && !schema.isEmpty()) ? schema : getSchema(dbType, databaseName, username);
        String fullTableName = actualSchema + "." + tableName;

        // 先获取列信息（含注释）
        try (Connection conn = ds.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet colRs = meta.getColumns(null, actualSchema, tableName, "%")) {
                while (colRs.next()) {
                    Map<String, Object> col = new LinkedHashMap<>();
                    col.put("columnName", colRs.getString("COLUMN_NAME"));
                    col.put("comments", colRs.getString("REMARKS"));
                    columnList.add(col);
                }
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
        DataSource ds = getDataSource(id, dbType, host, port, databaseName, username, password, sid);
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
