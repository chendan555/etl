# ETL Spring Boot 项目开发记录

## 项目概述
将原有基于 Tomcat + Pentaho Kettle 的 ETL 平台重构为 Spring Boot 单模块项目。

## 技术栈
- **后端**: JDK 8 + Spring Boot 2.7.18 + MyBatis-Plus 3.5.3.1 + DM8 数据库
- **前端**: Vue 2 + Element UI + jsPlumb (保持原有代码)
- **端口**: 后端 9056, 前端开发 8689

---

## 项目结构

```
d:\project\ai\vs\03\
├── backend/                         # Spring Boot 后端
│   ├── pom.xml
│   ├── lib/                         # DM8 JDBC 驱动 (需手动放入)
│   └── src/main/java/com/gx/etl/
│       ├── GxEtlApplication.java    # 启动类
│       ├── common/
│       │   └── Result.java          # 统一响应体
│       ├── config/
│       │   ├── MybatisPlusConfig.java
│       │   ├── WebConfig.java       # CORS + .do 后缀过滤器
│       │   └── DynamicDataSourceManager.java  # 动态数据源管理
│       ├── datasource/
│       │   ├── entity/
│       │   │   ├── DatabaseSource.java
│       │   │   ├── Category.java
│       │   │   └── FileServer.java
│       │   ├── mapper/
│       │   ├── service/
│       │   │   ├── DatabaseSourceService.java
│       │   │   ├── CategoryService.java
│       │   │   └── FileServerService.java
│       │   └── controller/
│       │       ├── DatabaseController.java      # /v1/center/database
│       │       ├── DatabaseApiController.java   # /api/database
│       │       ├── CategoryController.java      # /v1/center/database/categories
│       │       ├── CommonController.java        # /api/common
│       │       ├── FileServerController.java    # /api/data/source/file
│       │       └── DataSourceController.java    # /api/data/source
│       ├── flow/
│       │   ├── entity/
│       │   │   ├── Transformation.java
│       │   │   └── ExecutionLog.java
│       │   ├── service/
│       │   │   ├── TransformationService.java
│       │   │   ├── FlowChartNode.java
│       │   │   ├── DagParser.java
│       │   │   └── FlowExecutionEngine.java
│       │   └── controller/
│       │       ├── FlowController.java          # /api/transNew
│       │       └── PluginController.java        # /api/trans/plugin
│       └── operator/
│           ├── core/
│           │   ├── AbstractOperator.java
│           │   ├── OperatorRegistry.java
│           │   ├── OperatorSchema.java
│           │   ├── OperatorContext.java
│           │   ├── DataSet.java
│           │   └── DataRow.java
│           ├── input/
│           │   ├── TableInputOperator.java
│           │   ├── ExcelInputOperator.java
│           │   └── TextFileInputOperator.java
│           ├── output/
│           │   ├── TableOutputOperator.java
│           │   └── ExcelOutputOperator.java
│           └── transform/
│               ├── FilterRowsOperator.java
│               ├── SelectValuesOperator.java
│               ├── SortRowsOperator.java
│               ├── MergeJoinOperator.java
│               ├── CalculatorOperator.java
│               ├── ReplaceStringOperator.java
│               ├── UniqueOperator.java
│               ├── NormaliserOperator.java
│               ├── DenormaliserOperator.java
│               ├── ExecSQLOperator.java
│               └── JavaScriptCodeOperator.java
│
└── frontend/                        # Vue 前端 (从原项目复制)
    ├── package.json
    ├── vue.config.js
    └── src/
        ├── main.ts
        ├── api/
        │   ├── index.ts             # 主 API 文件
        │   └── modules/             # 各模块 API
        └── views/
            └── DataCenterN/         # 数据源管理页面
```

---

## 数据库表结构

```sql
-- 分类树
CREATE TABLE T_CATEGORY (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(200) NOT NULL,
    PARENT_ID BIGINT DEFAULT -1,
    SORT_ORDER INT DEFAULT 0,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 数据源
CREATE TABLE T_DATABASE (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(200) NOT NULL,
    DB_TYPE VARCHAR(50) NOT NULL,       -- DM, ORACLE, MYSQL, POSTGRESQL, KINGBASE
    HOST VARCHAR(200) NOT NULL,
    PORT INT NOT NULL,
    DATABASE_NAME VARCHAR(200),
    USERNAME VARCHAR(200),
    PASSWORD VARCHAR(500),
    SID VARCHAR(200),
    SCHEMA_FILTER VARCHAR(500),
    IS_INCLUDE CHAR(1) DEFAULT '1',
    CATEGORIES_ID BIGINT,              -- 1=源数据库, 2=引接库, 3=处理库
    STATUS INT DEFAULT 1,
    REMARK VARCHAR(500),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 文件服务器
CREATE TABLE T_FILE_SERVER (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(200) NOT NULL,
    SERVER_TYPE VARCHAR(50) NOT NULL,
    HOST VARCHAR(200) NOT NULL,
    PORT INT NOT NULL,
    USERNAME VARCHAR(200),
    PASSWORD VARCHAR(500),
    BASE_PATH VARCHAR(500),
    STATUS INT DEFAULT 1,
    REMARK VARCHAR(500),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 流程定义
CREATE TABLE T_TRANSFORMATION (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(200) NOT NULL,
    CATEGORIES_ID BIGINT,
    FLOW_JSON CLOB,
    IMG_URL CLOB,
    STATUS INT DEFAULT 0,
    REMARK VARCHAR(500),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 执行日志
CREATE TABLE T_EXECUTION_LOG (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    TRANS_ID BIGINT NOT NULL,
    TRANS_NAME VARCHAR(200),
    STATUS INT DEFAULT 0,              -- 0=运行中, 1=成功, 2=失败
    START_TIME TIMESTAMP,
    END_TIME TIMESTAMP,
    ERROR_MSG CLOB,
    ROW_COUNT BIGINT DEFAULT 0,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 算子分类
CREATE TABLE T_OPERATOR_CATEGORY (
    ID VARCHAR(50) PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    ICON VARCHAR(500),
    SORT_ORDER INT DEFAULT 0,
    TYPE INT DEFAULT 1
);

-- 算子注册
CREATE TABLE T_OPERATOR_TYPE (
    ID VARCHAR(50) PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    PLUGIN_ID VARCHAR(100) NOT NULL,
    CATEGORY_ID VARCHAR(50),
    ICON VARCHAR(500),
    DRAG_ICON VARCHAR(500),
    DESCRIPTION VARCHAR(500),
    IMPLEMENTATION VARCHAR(500),
    SORT_ORDER INT DEFAULT 0,
    TYPE INT DEFAULT 1,
    STATUS INT DEFAULT 1
);
```

---

## 已实现的接口

### 1. 数据源管理 (`/v1/center/database`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/list/page` | 分页查询 | ✅ |
| POST | `/save` | 保存/更新 | ✅ |
| POST | `/del` | 删除 | ✅ |
| POST | `/connect/dm/testConnect` | 测试连接 | ✅ |
| GET | `/connect/dm/getTables` | 获取表列表 | ✅ |
| POST | `/connect/dm/getColumns` | 获取字段 | ✅ |
| POST | `/explorer` | 浏览结构 | ✅ |
| GET | `/dbTypes` | 数据库类型 | ✅ |
| GET | `/databasetype/list` | 数据库类型(兼容) | ✅ |
| GET | `/databasecontype/list` | 连接类型 | ✅ |

### 2. 数据库API (`/api/database`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/getSchema` | 获取Schema | ✅ |
| POST | `/getSchemaAll` | 获取所有Schema | ✅ |
| POST | `/getDataTables` | 获取表列表 | ✅ |
| POST | `/getColumnsByTable` | 获取表字段 | ✅ |
| POST | `/explorer` | 浏览结构 | ✅ |
| POST | `/yj/getDataTables` | 引接库表列表 | ✅ |
| POST | `/yj/getTableDatas` | 引接库表数据 | ✅ |
| POST | `/yj/getValusByColumn` | 获取列值 | ✅ |

### 3. 分类管理 (`/v1/center/database/categories`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/tree` | 获取树 | ✅ |
| GET | `/list` | 获取列表 | ✅ |
| POST | `/save` | 保存 | ✅ |
| POST | `/del` | 删除 | ✅ |

### 4. 通用接口 (`/api/common`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/getDatabases` | 获取数据库列表 | ✅ |
| GET | `/getDatabasesByType` | 按类型获取 | ✅ |
| POST | `/CategoryCache/tree` | 分类树 | ✅ |
| POST | `/CategoryCache/save` | 保存分类 | ✅ |
| GET | `/CategoryCache/deleteBatchIds` | 删除分类 | ✅ |
| POST | `/CategoryCache/update` | 更新分类 | ✅ |

### 5. 文件服务器 (`/api/data/source/file`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/list` | 列表 | ✅ |
| POST | `/save/or/edit` | 保存 | ✅ |
| DELETE | `/delete/{id}` | 删除 | ✅ |

### 6. 流程管理 (`/api/transNew`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/steps` | 算子列表 | ✅ |
| POST | `/saveJSON` | 保存流程 | ✅ |
| POST | `/openNew` | 打开流程 | ✅ |
| POST | `/run` | 执行流程 | ✅ |
| POST | `/previewData` | 预览数据 | ✅ |
| POST | `/log/page` | 日志分页 | ✅ |
| POST | `/previousSteps` | 前置步骤 | ✅ |
| POST | `/prevSteps` | 前置步骤 | ✅ |
| POST | `/prevStepsFileds` | 前置字段 | ✅ |
| POST | `/nextSteps` | 后续步骤 | ✅ |
| POST | `/addStepName` | 添加步骤名 | ✅ |
| POST | `/updateStepName` | 更新步骤名 | ✅ |
| POST | `/files` | 文件列表 | ✅ |
| POST | `/previewFile` | 预览文件 | ✅ |
| POST | `/tableFields` | 表字段 | ✅ |
| POST | `/previewDataOut` | 预览输出 | ✅ |
| POST | `/getDataView` | 数据视图 | ✅ |
| POST | `/result` | 执行结果 | ✅ |
| POST | `/iputOutputFields` | 输入输出字段 | ✅ |
| POST | `/iputOutputFieldsName` | 字段名 | ✅ |
| POST | `/getFieldAndRow` | 字段行 | ✅ |
| POST | `/contendValue` | 内容值 | ✅ |
| POST | `/fieldType` | 字段类型 | ✅ |
| GET | `/downloadExcel` | 下载Excel | ✅ |
| POST | `/calcDesc` | 计算描述 | ✅ |
| POST | `/get/count` | 获取数量 | ✅ |
| POST | `/rule/page` | 规则分页 | ✅ |
| GET | `/log/del` | 删除日志 | ✅ |

### 7. 算子插件 (`/api/trans/plugin`)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/schema` | 获取Schema | ✅ |
| GET | `/find/field/type` | 字段类型 | ✅ |
| GET | `/check/table` | 检查表 | ✅ |

---

## 未实现的接口 (前端调用但后端无实现)

### 高优先级 (核心功能需要)
```
# 数据源管理相关
GET  /v1/center/database/page                    # 分页查询(路径不同)
POST /v1/center/database/create                  # 创建(路径不同)
POST /v1/center/database/update                  # 更新(路径不同)

# 流程执行相关
POST /api/transNew/rule/save.do                  # 保存规则
POST /api/transNew/rule/delete.do                # 删除规则
POST /api/transNew/rule/find.do                  # 查找规则
POST /api/transNew/rule/findTypeList.do          # 规则类型列表
```

### 中优先级 (扩展功能)
```
# 任务管理
POST /api/task/plan/import/task/node.do
GET  /api/task/plan/task/node/list.do
GET  /api/task/plan/map/info/{taskId}.do
POST /api/task/plan/save/both.do
DELETE /api/task/plan/delete/both.do

# 数据审核
POST /api/audit/rule/types.do
POST /api/audit/rule/page.do
POST /api/audit/rule/detail.do
POST /api/audit/rule/create.do
POST /api/audit/rule/edit.do
POST /api/audit/rule/audit.do

# 数据整编
GET  /api/trans/plan/categroy/cache/tree.do
POST /api/trans/plan/pageReorganizationByCondition
POST /api/trans/plan/update/reorganization.do

# 数据查询
GET  /api/trans/plan/page/data/query.do
GET  /api/trans/plan/get/out/detail.do
GET  /api/trans/plan/getDataType.do
GET  /api/trans/plan/getDataStatus.do
GET  /api/trans/plan/getFileType.do
```

### 低优先级 (可选功能)
```
# 用户管理 (已禁用登录，可忽略)
GET  /v1/auth/org/page
GET  /v1/auth/user/page
POST /v1/auth/user/save
GET  /v1/auth/roles/page
POST /v1/auth/users/del
POST /v1/auth/user/logout

# 数据接收
POST /api/yj/exec/table/page.do
POST /api/yj/exec/file/page.do
GET  /api/getBatchNumber
POST /api/receiveData

# 仿真数据
POST /api/es/imitate/call.do
POST /api/yj/record/simulation/page.do

# 工具集
POST /api/dll/exec/model/save.do
POST /api/dll/exec/list/page.do
POST /api/dll/exec/data/file/upload.do
GET  /api/dll/exec/model/del.do
POST /api/dll/exec/model/exec.do

# 表关系配置
POST /api/yj/tableConfig/queryListTableConfig.do
POST /api/yj/tableConfig/saveTableConfigAndDetail.do
DELETE /api/yj/tableConfig/deleteTableConfigAndDetail.do

# 协议解析
POST /api/yj/protocol/page.do
POST /api/yj/protocol/save.do
DELETE /api/yj/protocol/delete.do

# 表单模型
POST /api/yj/form/page.do
POST /api/yj/form/save.do
DELETE /api/yj/form/delete.do

# 报表统计
GET  /api/transNew/census/auditResult.do
GET  /api/transNew/census/runResult.do
GET  /api/transNew/census/categroy.do
GET  /api/transNew/census/allNums.do
GET  /api/transNew/census/checkNum.do
GET  /api/transNew/errorForm/page.do

# 首页统计
GET  /api/yj/index/config/merge/num.do
GET  /api/yj/index/config/exec/situation.do
GET  /api/yj/index/cumulative/volume.do
GET  /api/transNew/log/allNums.do
GET  /api/transNew/log/runResult.do
GET  /api/transNew/get/transformationByTask.do

# 分类管理
GET  /api/equip/classify/tree.do
GET  /api/equip/classify/info.do
DELETE /api/equip/classify/delete.do
PUT  /api/equip/classify/update.do
GET  /api/equip/classify/selectMaxId.do

# 方案管理
GET  /api/trans/plan/getTransByPlan.do
PUT  /api/trans/plan/update/type.do
POST /api/trans/plan/quit/type.do

# 项目管理
PUT  /api/repository/new/updateTransToModel.do
POST /api/repository/new/addCopyTrans.do
POST /api/task/group/getGroupList.do
PUT  /api/task/compiling/{id}.do
PUT  /api/task/may/compile/{id}.do

# 评估管理
POST /api/transNew/assess/find.do
POST /api/transNew/assess/save.do
POST /api/transNew/assess/page.do
POST /api/transNew/assess/delete.do
POST /api/transNew/assess/run.do

# 审核管理
POST /api/transNew/audit/page.do
POST /api/transNew/audit/find.do
POST /api/transNew/audit/auditById.do
POST /api/transNew/update/reorganization.do
```

---

## 已知问题

### 1. 前端依赖问题 (已解决)
- `vue-class-component` 未安装 → 已安装 v7.2.6
- `monaco-editor` 版本不兼容 TS 3.7.3 → 已降级到 v0.30.0
- `svg-sprite-loader` 未安装 → 已安装
- `kityMinder` 不可用 → 已在 main.ts 中注释掉

### 2. 后端编译问题 (已解决)
- `DataSet.forEach` 冲突 → 已移除自定义 forEach
- `SortRowsOperator` lambda 变量非 final → 已修复

### 3. API 路径问题
- 前端调用带 `.do` 后缀 → 后端有 DoSuffixFilter 处理
- 部分接口路径不一致 → 需要逐一适配

### 4. 数据库类型选择问题 (已修复)
- 类型分类下拉框被禁用 → 已移除 disabled 条件
- 选择类型后不自动填充 → 已添加 onDbTypeChange 方法

### 5. Schema 查询问题 (已修复)
- 返回用户名而非真实 Schema → 已改为查询数据库
- databaseName 为数字时被当作 Schema → 已过滤纯数字值

### 6. 分类保存问题 (已修复)
- 数据源保存到所有分类 → 已改为传递 categoriesId
- 前端发送 sourceId 而非 categoriesId → 已修复

---

## 启动步骤

### 后端
1. 将 DM8 JDBC 驱动放入 `backend/lib/`
2. 修改 `application.yml` 数据库配置
3. 执行 `schema.sql` 建表
4. 启动 `GxEtlApplication.java`

### 前端
```bash
cd frontend
npm install
npm run dev
```

---

## 添加新算子步骤

1. 在 `operator/transform/` 或 `operator/input/` 或 `operator/output/` 下创建新类
2. 继承 `AbstractOperator`
3. 实现以下方法:
   - `getId()` - 返回唯一标识 (如 "MyOperator")
   - `getName()` - 返回显示名称 (如 "我的算子")
   - `getCategory()` - 返回分类 (input/output/transform/script)
   - `getSchema()` - 返回配置 Schema
   - `execute()` - 实现执行逻辑
4. 添加 `@Component` 注解
5. 在 `schema.sql` 的 `T_OPERATOR_TYPE` 表中插入记录
6. 重启后端

示例:
```java
@Component
public class MyOperator extends AbstractOperator {
    @Override
    public String getId() { return "MyOperator"; }
    
    @Override
    public String getName() { return "我的算子"; }
    
    @Override
    public String getCategory() { return "transform"; }
    
    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
            .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
            .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true);
    }
    
    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        // 实现逻辑
        return input;
    }
}
```

---

## 下次开发优先级

### P0 (必须完成)
1. 补充所有前端调用的接口 (至少返回空数据，避免 404)
2. 确保数据源管理 CRUD 完整可用
3. 确保流程设计器保存/加载/执行正常

### P1 (重要功能)
1. 任务管理接口
2. 数据审核接口
3. 数据整编接口
4. 数据查询接口

### P2 (可选功能)
1. 用户管理 (当前已禁用登录)
2. 数据接收
3. 仿真数据
4. 报表统计
5. 工具集

---

## 注意事项

1. **DM8 驱动**: 必须手动放入 `backend/lib/` 目录
2. **数据库连接**: 修改 `application.yml` 中的 DM8 连接信息
3. **建表脚本**: 执行 `schema.sql` 创建表和初始数据
4. **前端代理**: `vue.config.js` 中代理已配置指向 9056
5. **.do 后缀**: 后端有 DoSuffixFilter 自动处理
6. **响应格式**: 统一使用 `Result.ok()` 或 `Result.fail()`
7. **字段映射**: 前端字段名可能与后端不同，需要在 Controller 中做映射
