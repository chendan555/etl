# 引接性能测试数据

模拟「多实例 × 单实例几十万~上百万行」的引接场景，验证实例级并行 + 提交策略优化后的吞吐和进度条表现。

## 测试数据构成

| 项 | 值 |
|---|---|
| 测试主题 | 引接性能测试（`TOPIC_INGEST_PERF_TEST`），13 个字段，仿弹道数据，全 simpleType |
| 达梦目标表 | `CASIC_ASSESS.t_topic_ingest_perf_test`（全 VARCHAR(500)，仿现有目标表风格；数据表所在 schema 见 CONTEXT.md「数据库 schema 约定」） |
| MySQL 实例表 | `data_collect.t990000001` ~ `t990000010`，共 10 个 |
| 数据量 | 实例1=100万行，实例2=80万行，实例3~10=各60万行，**合计 660 万行** |
| 载荷格式 | msgpack 13 元素数组（与字段 arrayIndex 一一对应），每行 topic=`TOPIC_INGEST_PERF_TEST` |
| 测试方案 | 方案名 **性能测试-660万行**（STATUS=0），2 个样本明细 × 各 5 个实例 |

配置表写入 `CASIC_ASSESS` schema（与系统一致），数据表在 `ETL` schema。不触碰任何真实主题/方案/数据。

## 怎么跑

1. 确认后端已用优化后的代码重启（并行度生效：`scheme.ingest.parallel-size: 6`）；
2. 前端打开 **方案管理**，找到「性能测试-660万行」→ 点 **引接**；
3. 观察进度弹窗：total 应为 **10**（实例数，不是样本数 2），进度按实例推进；
4. 后端日志关注：`引接任务: ... 实例任务10个, 并行线程6`、各实例 `实例处理完成: ... 写入N条` 的先后交错（应 6 路并发）。

## 验收参考

- 6 线程并发下，第一波 6 个实例同时跑，其余 4 个排队接续；
- 达梦侧每 5 万行一次 commit（一个 60 万行实例约 12 次提交，日志/性能上不再有 600 次提交）；
- 全部完成后方案 STATUS=1，`CASIC_ASSESS.t_topic_ingest_perf_test` 行数 = 660 万。

## 重新生成 / 调规模

改 `Gen.java` 里的 `INSTANCES` 数组（实例ID、行数）后重跑。

**注意编码**：源码是 UTF-8，Java 17 在中文 Windows 上默认按 GBK 编译，必须显式 `-encoding UTF-8`，否则中文字面量写进库里就是乱码（第一版数据踩过的坑）：

```
cd test/ingest-testdata
set JARS=C:\Users\chendan\.m2\repository\com\dameng\DmJdbcDriver18\8.1.3.140\DmJdbcDriver18-8.1.3.140.jar;C:\Users\chendan\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;C:\Users\chendan\.m2\repository\org\msgpack\msgpack-core\0.8.20\msgpack-core-0.8.20.jar
javac -encoding UTF-8 -cp "%JARS%" -d classes Gen.java Cleanup.java
java -Dfile.encoding=UTF-8 -cp "classes;%JARS%" Gen
```

清理（测试完执行）：

```
java -Dfile.encoding=UTF-8 -cp "classes;%JARS%" Cleanup
```

可重复执行：清理按结构关联（主题代码/数字ID）匹配，不依赖中文名称，历史乱码行也能清掉。

## 流程性能测试（处理引接表）

`GenFlow.java` 构建一条测试流程「性能测试-引接表处理」：表输入（按 `${schemeId}` 参数读 `t_topic_ingest_perf_test`）→ 表输出（写 `t_perf_test_output`，提交数量 5000），用于验证流程批处理提速与多批并行。

```
javac -encoding UTF-8 -cp "%JARS%;fastjson-1.2.47.jar" -d classes GenFlow.java
java -Dfile.encoding=UTF-8 -cp "classes;%JARS%;fastjson-1.2.47.jar" GenFlow
```

用法：旧版 FlowChart 打开该流程 → 参数执行 → 添加参数行（每行 `{"schemeId": "990000003"}` 等）→ 执行，进度看终端「批量进度」。先跑 1 行验证，再加到 4 行看并行。重跑生成器会先清理旧流程/输出表。
