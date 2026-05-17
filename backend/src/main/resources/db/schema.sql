-- ============================
-- 分类树
-- ============================
CREATE TABLE T_CATEGORY (
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME        VARCHAR(200) NOT NULL,
    PARENT_ID   BIGINT DEFAULT -1,
    SORT_ORDER  INT DEFAULT 0,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 数据源（数据库连接）
-- ============================
CREATE TABLE T_DATABASE (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME            VARCHAR(200) NOT NULL,
    DB_TYPE         VARCHAR(50) NOT NULL,       -- DM, ORACLE, MYSQL, POSTGRESQL, KINGBASE
    HOST            VARCHAR(200) NOT NULL,
    PORT            INT NOT NULL,
    DATABASE_NAME   VARCHAR(200),
    USERNAME        VARCHAR(200),
    PASSWORD        VARCHAR(500),
    SID             VARCHAR(200),               -- Oracle SID
    SCHEMA_FILTER   VARCHAR(500),               -- 逗号分隔的 schema 过滤
    IS_INCLUDE      CHAR(1) DEFAULT '1',        -- 1=包含, 0=排除
    CATEGORIES_ID   BIGINT,                     -- 所属分类
    STATUS          INT DEFAULT 1,              -- 1=启用, 0=禁用
    REMARK          VARCHAR(500),
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 文件服务器
-- ============================
CREATE TABLE T_FILE_SERVER (
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME        VARCHAR(200) NOT NULL,
    SERVER_TYPE VARCHAR(50) NOT NULL,           -- FTP, SFTP, SMB
    HOST        VARCHAR(200) NOT NULL,
    PORT        INT NOT NULL,
    USERNAME    VARCHAR(200),
    PASSWORD    VARCHAR(500),
    BASE_PATH   VARCHAR(500),
    STATUS      INT DEFAULT 1,
    REMARK      VARCHAR(500),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 转换（流程定义）
-- ============================
CREATE TABLE T_TRANSFORMATION (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME            VARCHAR(200) NOT NULL,
    CATEGORIES_ID   BIGINT,                     -- 所属分类
    FLOW_JSON       CLOB,                       -- jsPlumb 完整 JSON
    IMG_URL         CLOB,                       -- 画布缩略图
    STATUS          INT DEFAULT 0,              -- 0=草稿, 1=已发布
    REMARK          VARCHAR(500),
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 执行日志
-- ============================
CREATE TABLE T_EXECUTION_LOG (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    TRANS_ID        BIGINT NOT NULL,
    TRANS_NAME      VARCHAR(200),
    STATUS          INT DEFAULT 0,              -- 0=运行中, 1=成功, 2=失败
    START_TIME      TIMESTAMP,
    END_TIME        TIMESTAMP,
    ERROR_MSG       CLOB,
    ROW_COUNT       BIGINT DEFAULT 0,
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 算子分类
-- ============================
CREATE TABLE T_OPERATOR_CATEGORY (
    ID          VARCHAR(50) PRIMARY KEY,
    NAME        VARCHAR(100) NOT NULL,
    ICON        VARCHAR(500),
    SORT_ORDER  INT DEFAULT 0,
    TYPE        INT DEFAULT 1                  -- 1=转换
);

-- ============================
-- 算子注册
-- ============================
CREATE TABLE T_OPERATOR_TYPE (
    ID              VARCHAR(50) PRIMARY KEY,
    NAME            VARCHAR(100) NOT NULL,
    PLUGIN_ID       VARCHAR(100) NOT NULL,      -- 唯一标识，如 TableInput
    CATEGORY_ID     VARCHAR(50),                -- 所属分类
    ICON            VARCHAR(500),
    DRAG_ICON       VARCHAR(500),
    DESCRIPTION     VARCHAR(500),
    IMPLEMENTATION  VARCHAR(500),               -- Java 实现类全名
    DOM             CLOB,                       -- 设计器DOM JSON
    SORT_ORDER      INT DEFAULT 0,
    TYPE            INT DEFAULT 1,              -- 1=转换
    STATUS          INT DEFAULT 1
);

-- 初始分类数据
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('input', '输入', '', 1);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('output', '输出', '', 2);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('transform', '转换', '', 3);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('script', '脚本', '', 4);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('category0', '输入', '', 1);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('category32', '输出', '', 2);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('category49', '转换', '', 3);
INSERT INTO T_OPERATOR_CATEGORY (ID, NAME, ICON, SORT_ORDER) VALUES ('category50', '脚本', '', 4);
COMMIT;

-- 算子数据请执行 migration_operator_type.sql（从GXETL_R_PLUGIN_TYPE迁移）