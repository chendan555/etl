# 数据库脚本

当前库：达梦 `CASIC_ASSESS` schema（旧 `ETL` schema 已废弃）。

## 其他电脑恢复步骤

1. 建库/建用户后，先执行结构：

   - `schema_all.sql` — 全部 `T_*` 配置表的 `CREATE TABLE`

2. 再按需导入数据（UTF-8）：

   - `CASIC_ASSESS_T_*.sql` — 各表 `INSERT`（由 `test/ingest-testdata/DumpDb.java` 导出）

   常用配置表优先导入：

   - `T_DATABASE` / `T_OPERATOR_CATEGORY` / `T_OPERATOR_TYPE`
   - `T_CATEGORY` / `T_CJ_CONFIG` / `T_SAMPLE_*` / `T_SCHEME*`
   - `T_TOPIC_CONFIG` / `T_TOPIC_FIELD`
   - `T_TRANSFORMATION`（流程 JSON，体积较大）
   - 引接相关 `T_YJ_*`

   日志类表（`T_EXECUTION_LOG`、`T_YJ_EXEC_LOG`、`T_YJ_HISTORY`）可跳过。

3. 应用配置见 `backend/src/main/resources/application.yml`：

   - `scheme.ingest.dm-schema: CASIC_ASSESS`

## 重新导出

```bash
javac -encoding UTF-8 -cp backend/lib/DmJdbcDriver18.jar -d test/ingest-testdata/classes test/ingest-testdata/DumpDb.java
java -cp "test/ingest-testdata/classes;backend/lib/DmJdbcDriver18.jar" DumpDb 数据库
```

连接信息写在 `DumpDb.java` 顶部常量。

## 历史文件

- `ETL_*.sql`、`1.sql`、`ETL_TABLE_*.sql`：迁移前的旧导出，仅作对照，**不要**再导入当前库。
