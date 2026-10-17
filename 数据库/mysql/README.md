# MySQL 引接/仿真库导出

导出时间：见各文件头 `mysqldump` 注释。连接：`127.0.0.1:3306`，`root/root`。

## 库一览

| 库 | 用途 | 文件 |
|----|------|------|
| `data_collect` | 引接源库，实例表 `t990000001`~`t990000010` 等 | `data_collect_schema.sql` + `data_collect_small_data.sql` + `large/t99*.sql.gz` |
| `big_sample_db` | 仿真方案 `experiment_design` | `big_sample_db_*.sql` |
| `modelmanager` | 主题/字段导入源 | `modelmanager_*.sql` |
| `indicator_db` | 指标节点 | `indicator_db_*.sql` |
| `sim_control_db_copy` | 任务设计示例 | `sim_control_db_copy_*.sql` |

`jeecg-boot` 等与引接无关的库未导出。

## 其他电脑导入

```bash
# 1) 建库并导入结构（含 data_collect 大表结构）
mysql -uroot -p < 数据库/mysql/data_collect_schema.sql
mysql -uroot -p < 数据库/mysql/big_sample_db_schema.sql
mysql -uroot -p < 数据库/mysql/modelmanager_schema.sql
mysql -uroot -p < 数据库/mysql/indicator_db_schema.sql
mysql -uroot -p < 数据库/mysql/sim_control_db_copy_schema.sql

# 2) 小表数据（配置/仿真方案/模型）
mysql -uroot -p data_collect < 数据库/mysql/data_collect_small_data.sql
mysql -uroot -p big_sample_db < 数据库/mysql/big_sample_db_data.sql
mysql -uroot -p modelmanager < 数据库/mysql/modelmanager_data.sql
mysql -uroot -p indicator_db < 数据库/mysql/indicator_db_data.sql
mysql -uroot -p sim_control_db_copy < 数据库/mysql/sim_control_db_copy_data.sql

# 3) 大表数据（每张约千万级行，按需）
gzip -dc 数据库/mysql/large/t990000001.sql.gz | mysql -uroot -p data_collect
# … 其余 t990000002 ~ t990000010 同理
```

PowerShell 下解压导入示例：

```powershell
Get-Content 数据库\mysql\large\t990000001.sql.gz -Encoding Byte |
  ForEach-Object { $_ } | Set-Content -Encoding Byte tmp.gz
# 或使用 7-Zip / tar 解出 .sql 后：
mysql -uroot -p data_collect < t990000001.sql
```

## 说明

- 大表总数据约 850MB，`large/*.sql.gz` 合计约 **173MB**。
- 只测引接链路时可只导入 1 张 `t99*` 或改用 `t123/t456/t789` 小表。
- 重新导出：对每个库执行 `mysqldump --default-character-set=utf8mb4 -h127.0.0.1 -P3306 -uroot -proot <库名> [表…]`。
