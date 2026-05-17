-- ============================
-- 任务分组表
-- ============================
CREATE TABLE T_TASK_GROUP (
    ID_TASK_GROUP   BIGINT PRIMARY KEY AUTO_INCREMENT,
    TASK_GROUP_NAME VARCHAR(200) NOT NULL,
    TASK_GROUP_CODE VARCHAR(200),
    TASK_DESC       VARCHAR(500),
    TYPE            VARCHAR(10),                    -- '1'=模板, '2'=预处理, '3'=挖掘分析
    PARENT_ID       BIGINT DEFAULT -1,              -- 父节点ID, -1表示根节点
    IS_ROOT         INT DEFAULT 0,                  -- 0=否, 1=是
    COUNT_TRANS     INT DEFAULT 0,                  -- 关联的流程数
    SORT_ORDER      INT DEFAULT 0,
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 方案分组表
-- ============================
CREATE TABLE T_PLAN_CATEGORY (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME            VARCHAR(200) NOT NULL,
    DESCS           VARCHAR(500),
    PARENT_ID       BIGINT DEFAULT -1,
    SORT_ORDER      INT DEFAULT 0,
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 方案主表
-- ============================
CREATE TABLE T_TRANS_PLAN (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME            VARCHAR(200) NOT NULL,
    GROUP_ID        BIGINT,                         -- 所属分组ID
    STATUS          VARCHAR(10) DEFAULT '1',        -- '1'=未执行, '2'=已执行
    FLAG            INT DEFAULT 0,
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 方案详情表（关联流程和引接）
-- ============================
CREATE TABLE T_PLAN_DETAIL (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    PLAN_ID         BIGINT NOT NULL,                -- 关联方案ID
    SOURCE_ID       VARCHAR(200),                   -- 源ID (transformationId 或 引接ID)
    DETAIL_TYPE     VARCHAR(10) NOT NULL,           -- 'cl'=处理流程, 'yj'=引接
    KETTLE_NAME     VARCHAR(500),                   -- 流程文件名/URL
    GROUP_NAME      VARCHAR(200),                   -- 所属分组名
    TYPE            VARCHAR(50),                    -- 类型
    REMARK          VARCHAR(500),                   -- 描述
    AGREEMENT_NAME  VARCHAR(200),                   -- 解析协议名称
    SORT_ORDER      INT DEFAULT 0,
    CREATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- 初始数据：任务分组根节点
-- ============================
INSERT INTO T_TASK_GROUP (ID_TASK_GROUP, TASK_GROUP_NAME, TASK_GROUP_CODE, TASK_DESC, TYPE, PARENT_ID, IS_ROOT, SORT_ORDER)
VALUES (1, '模板', 'template', '模板分组', '1', -1, 1, 1);

INSERT INTO T_TASK_GROUP (ID_TASK_GROUP, TASK_GROUP_NAME, TASK_GROUP_CODE, TASK_DESC, TYPE, PARENT_ID, IS_ROOT, SORT_ORDER)
VALUES (2, '预处理', 'preprocess', '预处理分组', '2', -1, 1, 2);

INSERT INTO T_TASK_GROUP (ID_TASK_GROUP, TASK_GROUP_NAME, TASK_GROUP_CODE, TASK_DESC, TYPE, PARENT_ID, IS_ROOT, SORT_ORDER)
VALUES (3, '挖掘分析', 'analysis', '挖掘分析分组', '3', -1, 1, 3);

COMMIT;
