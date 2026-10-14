import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * 导出达梦 CASIC_ASSESS 配置表结构 + 数据到 数据库/ 目录。
 * 用法: java -cp ".;DmJdbcDriver18.jar" DumpDb [输出目录]
 */
public class DumpDb {
    static final String URL = "jdbc:dm://127.0.0.1:5236?compatibleMode=mysql";
    static final String USER = "CASIC_ASSESS";
    static final String PWD = "123456789";
    static final String[] CANDIDATE_SCHEMA = { "CASIC_ASSESS", "ETL", "SYSDBA" };
    /** 只导出配置类表，跳过 topic 大数据表 */
    static final String TABLE_PREFIX = "T_";

    public static void main(String[] args) throws Exception {
        Path outDir = Paths.get(args.length > 0 ? args[0] : "数据库");
        Files.createDirectories(outDir);

        try (Connection c = DriverManager.getConnection(URL, USER, PWD)) {
            String schema = findSchema(c);
            System.out.println("schema=" + schema);

            List<String> tables = listTables(c, schema);
            System.out.println("tables=" + tables.size());

            // 1) 全量 DDL
            Path ddlPath = outDir.resolve("schema_all.sql");
            try (Writer w = Files.newBufferedWriter(ddlPath, StandardCharsets.UTF_8)) {
                w.write("-- 自动生成：DumpDb " + new java.util.Date() + "\n");
                w.write("-- schema=" + schema + "\n\n");
                for (String t : tables) {
                    w.write(readTableDdl(c, schema, t));
                    w.write("\n\n");
                }
            }
            System.out.println("wrote " + ddlPath);

            // 2) 每表 INSERT（配置数据）
            for (String t : tables) {
                Path p = outDir.resolve(schema + "_" + t + ".sql");
                int n = dumpInserts(c, schema, t, p);
                System.out.println("wrote " + p + " rows=" + n);
            }
        }
    }

    static String findSchema(Connection c) throws Exception {
        for (String s : CANDIDATE_SCHEMA) {
            try (Statement st = c.createStatement()) {
                st.executeQuery("SELECT 1 FROM " + s + ".T_DATABASE WHERE 1=0");
                return s;
            } catch (Exception ignore) { }
        }
        return "CASIC_ASSESS";
    }

    static List<String> listTables(Connection c, String schema) throws Exception {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = c.getMetaData().getTables(null, schema, "%", new String[]{ "TABLE" })) {
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                if (name != null && name.startsWith(TABLE_PREFIX)) {
                    tables.add(name);
                }
            }
        }
        Collections.sort(tables);
        return tables;
    }

    /**
     * 用 JDBC 元数据拼 CREATE TABLE，兼容达梦。
     */
    static String readTableDdl(Connection c, String schema, String table) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE \"").append(schema).append("\".\"").append(table).append("\" (\n");
        List<String> cols = new ArrayList<>();
        List<String> pkCols = new ArrayList<>();
        try (ResultSet rs = c.getMetaData().getColumns(null, schema, table, "%")) {
            while (rs.next()) {
                String col = rs.getString("COLUMN_NAME");
                String type = rs.getString("TYPE_NAME");
                int size = rs.getInt("COLUMN_SIZE");
                int digits = rs.getInt("DECIMAL_DIGITS");
                int nullable = rs.getInt("NULLABLE");
                String def = rs.getString("COLUMN_DEF");
                boolean isIdent = false;
                try {
                    // 达梦可能提供 IS_AUTOINCREMENT
                    isIdent = "YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT"));
                } catch (Exception ignore) { }

                StringBuilder line = new StringBuilder("  \"").append(col).append("\" ").append(type);
                if ("DECIMAL".equalsIgnoreCase(type) || "NUMERIC".equalsIgnoreCase(type)) {
                    if (size > 0) line.append("(").append(size);
                    if (digits > 0) line.append(",").append(digits);
                    if (size > 0) line.append(")");
                } else if ("VARCHAR".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type)
                        || "VARCHAR2".equalsIgnoreCase(type) || "NVARCHAR".equalsIgnoreCase(type)) {
                    if (size > 0) line.append("(").append(size).append(")");
                }
                if (isIdent) {
                    line.append(" IDENTITY(1,1)");
                }
                if (nullable == DatabaseMetaData.columnNoNulls) {
                    line.append(" NOT NULL");
                }
                if (def != null && !def.trim().isEmpty() && !isIdent) {
                    line.append(" DEFAULT ").append(def.trim());
                }
                cols.add(line.toString());
            }
        }
        try (ResultSet rs = c.getMetaData().getPrimaryKeys(null, schema, table)) {
            while (rs.next()) {
                pkCols.add(rs.getString("COLUMN_NAME"));
            }
        }
        if (!cols.isEmpty()) {
            sb.append(String.join(",\n", cols));
        }
        if (!pkCols.isEmpty()) {
            sb.append(",\n  CLUSTER PRIMARY KEY(");
            for (int i = 0; i < pkCols.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(pkCols.get(i)).append("\"");
            }
            sb.append(") ENABLE");
        }
        sb.append("\n);");
        return sb.toString();
    }

    static int dumpInserts(Connection c, String schema, String table, Path out) throws Exception {
        String fq = "\"" + schema + "\".\"" + table + "\"";
        int count = 0;
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + fq);
             Writer w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {

            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            List<String> colNames = new ArrayList<>();
            List<Boolean> identityCols = new ArrayList<>();
            boolean hasIdentity = false;
            for (int i = 1; i <= n; i++) {
                colNames.add(md.getColumnName(i));
                boolean ident = false;
                try {
                    // 判断自增：达梦可能用 IS_AUTOINCREMENT，不可用则根据列名/类型启发式
                    String ai = null;
                    try (ResultSet ir = c.getMetaData().getColumns(null, schema, table, md.getColumnName(i))) {
                        if (ir.next()) {
                            ai = ir.getString("IS_AUTOINCREMENT");
                        }
                    }
                    ident = "YES".equalsIgnoreCase(ai);
                } catch (Exception ignore) { }
                identityCols.add(ident);
                if (ident) hasIdentity = true;
            }

            w.write("SET IDENTITY_INSERT \"" + schema + "\".\"" + table + "\" ON;\n");
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO \"").append(schema).append("\".\"").append(table).append("\"(");
                for (int i = 1; i <= n; i++) {
                    if (i > 1) sb.append(",");
                    sb.append("\"").append(colNames.get(i - 1)).append("\"");
                }
                sb.append(") VALUES(");
                for (int i = 1; i <= n; i++) {
                    if (i > 1) sb.append(",");
                    sb.append(toSqlLiteral(rs, md.getColumnType(i), i));
                }
                sb.append(");");
                w.write(sb.toString());
                w.write("\n");
                count++;
            }
            w.write("SET IDENTITY_INSERT \"" + schema + "\".\"" + table + "\" OFF;\n");
            if (hasIdentity) {
                // keep ON/OFF even if no identity, matches existing dump style
            }
        }
        return count;
    }

    static String toSqlLiteral(ResultSet rs, int sqlType, int idx) throws Exception {
        Object v = null;
        switch (sqlType) {
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE: {
                Timestamp ts = rs.getTimestamp(idx);
                return ts == null ? "null" : "'" + ts.toString().replace(' ', ' ') + "'";
            }
            case Types.DATE: {
                java.sql.Date d = rs.getDate(idx);
                return d == null ? "null" : "'" + d + "'";
            }
            case Types.BLOB:
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY: {
                byte[] b = rs.getBytes(idx);
                return b == null ? "null" : "/*binary:" + b.length + "*/null";
            }
            case Types.CLOB:
            case Types.NCLOB: {
                String s = rs.getString(idx);
                return s == null ? "null" : "'" + s.replace("'", "''") + "'";
            }
            default: {
                v = rs.getObject(idx);
                if (v == null) return "null";
                if (v instanceof Number) return v.toString();
                if (v instanceof Boolean) return ((Boolean) v) ? "1" : "0";
                String s = v.toString();
                return "'" + s.replace("'", "''") + "'";
            }
        }
    }
}
