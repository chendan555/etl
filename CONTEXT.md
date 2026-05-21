# ETL 项目领域术语

## 核心概念

- **引接（Ingestion/Receive）**: 将外部数据（数据库表或文件）导入到目标数据源的过程
- **数据引接配置（DataReceiveConfig）**: 配置数据库表到表的引接关系，包括源数据源、目标数据源、表映射
- **文件引接配置（FileReceiveConfig）**: 配置文件到数据库表的引接关系，包括文件路径、解析协议、目标数据源
- **批量数据引接（DataReceive）**: 执行引接操作的模块，可选择已配置的引接项批量执行
- **引接记录查询（ReceiveRecords）**: 查询历史引接执行记录、查看详情
- **解析协议（YjProtocol）**: 定义文件解析规则，包括分隔符、文件类型、列映射、ID策略
- **批次号（Batch Number）**: 每次引接执行的唯一标识，用于追踪和回溯

## 流程设计相关

- **算子（Operator）**: 流程设计中的处理节点，70+ 种，分为输入/输出/转换/脚本
- **流程（Transformation/Flow）**: 由多个算子通过 DAG 连接组成的数据处理流程
- **数据集（DataSet）**: 算子之间传递的数据结构，由 DataRow 组成

## 技术约定

- 前端：Vue 2 + jsPlumb + Element UI + TypeScript
- 后端：Spring Boot 2.7.18 + MyBatis-Plus + DM8 数据库
- 引接模块和流程设计模块是独立的两套代码体系
