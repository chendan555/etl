package com.gx.etl.scheme.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.sample.entity.SampleData;
import com.gx.etl.sample.entity.SampleDetail;
import com.gx.etl.sample.entity.SamplePlan;
import com.gx.etl.sample.entity.SampleSubData;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.mapper.TransformationMapper;
import com.gx.etl.scheme.entity.*;
import com.gx.etl.scheme.mapper.CjConfigMapper;
import com.gx.etl.scheme.mapper.SchemeMapper;
import com.gx.etl.scheme.mapper.TopicConfigMapper;
import com.gx.etl.scheme.mapper.TopicFieldMapper;
import com.gx.etl.datasource.mapper.DatabaseSourceMapper;
import com.gx.etl.scheme.service.SchemeService;
import com.gx.etl.scheme.service.TopicImportService;
import com.gx.etl.scheme.vo.SchemeDetailDataVO;
import com.gx.etl.scheme.vo.SchemeEquipVO;
import com.gx.etl.scheme.service.SchemeProcessService;
import com.gx.etl.sample.service.SampleCascadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.mapper.DatabaseSourceMapper;
import com.gx.etl.config.DynamicDataSourceManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheme")
public class SchemeController {

    private static final Logger log = LoggerFactory.getLogger(SchemeController.class);

    /** 达梦数据表所在 schema：数据表已随库迁移至 CASIC_ASSESS（原 ETL schema 已废弃），与 SchemeProcessService 保持一致 */
    @Value("${scheme.ingest.dm-schema:CASIC_ASSESS}")
    private String dmSchema;

    @Resource
    private SchemeService schemeService;

    @Resource
    private SchemeMapper schemeMapper;

    @Resource
    private TopicImportService topicImportService;

    @Resource
    private DatabaseSourceMapper databaseSourceMapper;

    @Resource
    private SampleCascadeService sampleCascadeService;

    @Resource
    private SchemeProcessService schemeProcessService;

    @Resource
    private TopicConfigMapper topicConfigMapper;

    @Resource
    private TopicFieldMapper topicFieldMapper;

    @Resource
    private CjConfigMapper cjConfigMapper;

    @Resource
    private TransformationMapper transformationMapper;

    @Resource
    private DataSource dataSource;

    // ========== 主题配置 CRUD ==========

    @GetMapping("/topics")
    public Result<List<TopicConfig>> getTopics() {
        return Result.ok(topicConfigMapper.selectList(null));
    }

    @GetMapping("/topic/page")
    public Result<?> getTopicPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String topicName) {
        Page<TopicConfig> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<TopicConfig> wrapper = new LambdaQueryWrapper<>();
        if (topicName != null && !topicName.isEmpty()) {
            wrapper.like(TopicConfig::getTopicName, topicName);
        }
        wrapper.orderByDesc(TopicConfig::getCreateTime);
        page = topicConfigMapper.selectPage(page, wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @PostMapping("/topic/save")
    public Result<?> saveTopic(@RequestBody TopicConfig topic) {
        // 保存主题
        if (topic.getId() != null) {
            topicConfigMapper.updateById(topic);
        } else {
            topicConfigMapper.insert(topic);
        }

        Long topicId = topic.getId();

        // 保存字段列表
        if (topic.getFieldList() != null) {
            // 删除旧字段
            topicFieldMapper.delete(
                    new LambdaQueryWrapper<TopicField>().eq(TopicField::getTopicId, topicId)
            );
            // 插入新字段
            for (int i = 0; i < topic.getFieldList().size(); i++) {
                TopicField field = topic.getFieldList().get(i);
                field.setId(null);
                field.setTopicId(topicId);
                field.setSortOrder(i);
                topicFieldMapper.insert(field);
            }
        }

        // 执行建表 DDL
        if (topic.getTableName() != null && !topic.getTableName().isEmpty()
                && topic.getFieldList() != null && !topic.getFieldList().isEmpty()) {
            String tableComment = topic.getTopicName() != null ? topic.getTopicName() : topic.getTableName();
            createDynamicTable(topic.getTableName(), topic.getFieldList(), tableComment);
        }

        return Result.ok();
    }

    private void createDynamicTable(String tableName, List<TopicField> fields, String tableComment) {
        StringBuilder createSb = new StringBuilder();
        createSb.append("CREATE TABLE ").append(tableName).append(" (");
        createSb.append("SCHEME_ID VARCHAR(500)");
        for (int i = 0; i < fields.size(); i++) {
            createSb.append(", ");
            createSb.append(fields.get(i).getFieldName()).append(" VARCHAR(500)");
        }
        createSb.append(")");
        String createSql = createSb.toString();
        log.info("执行建表DDL: {}", createSql);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // 达梦不支持 DROP TABLE IF EXISTS，先尝试删除
            try {
                stmt.execute("DROP TABLE " + tableName);
            } catch (Exception ignore) {
                // 表不存在时忽略
            }
            stmt.execute(createSql);

            // 添加表注释
            stmt.execute("COMMENT ON TABLE " + tableName + " IS '" + tableComment + "'");

            // 添加列注释
            for (TopicField field : fields) {
                if (field.getFieldAlias() != null && !field.getFieldAlias().isEmpty()) {
                    stmt.execute("COMMENT ON COLUMN " + tableName + "." + field.getFieldName()
                            + " IS '" + field.getFieldAlias() + "'");
                }
            }

            log.info("建表成功: {}", tableName);
        } catch (Exception e) {
            log.error("建表失败: {}", tableName, e);
        }
    }

    @GetMapping("/topic/detail")
    public Result<TopicConfig> getTopicDetail(@RequestParam Long id) {
        TopicConfig topic = topicConfigMapper.selectById(id);
        if (topic == null) return Result.fail("主题不存在");
        List<TopicField> fields = topicFieldMapper.selectList(
                new LambdaQueryWrapper<TopicField>()
                        .eq(TopicField::getTopicId, id)
                        .orderByAsc(TopicField::getSortOrder)
        );
        topic.setFieldList(fields);
        return Result.ok(topic);
    }

    @PostMapping("/topic/delete")
    public Result<?> deleteTopic(@RequestParam Long id) {
        // 查询主题获取表名
        TopicConfig topic = topicConfigMapper.selectById(id);
        if (topic != null && topic.getTableName() != null && !topic.getTableName().isEmpty()) {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("DROP TABLE " + topic.getTableName());
                log.info("删除表成功: {}", topic.getTableName());
            } catch (Exception e) {
                log.warn("删除表失败(可能不存在): {}", topic.getTableName(), e.getMessage());
            }
        }
        // 删除关联字段
        topicFieldMapper.delete(
                new LambdaQueryWrapper<TopicField>().eq(TopicField::getTopicId, id)
        );
        topicConfigMapper.deleteById(id);
        return Result.ok();
    }

    // ========== 从MySQL模型库导入主题 ==========

    /**
     * 预览MySQL模型库主题清单（不落库）。
     * 逻辑移植自 DataCollectWeb：查模型库 topic 表 + simulatmutual/dataitem 字段结构。
     * 主题名称取备注汉字，主题别名为技术名（可加/去前缀）。
     */
    @GetMapping("/topic/mysql/preview")
    public Result<?> previewMysqlTopics(@RequestParam Long dbId, @RequestParam String modelDb,
                                        @RequestParam(required = false, defaultValue = "true") boolean addPrefix,
                                        @RequestParam(required = false, defaultValue = "true") boolean stripPrefix,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                        @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        try {
            return Result.ok(topicImportService.preview(dbId, modelDb, addPrefix, stripPrefix, keyword, pageIndex, pageSize));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("预览MySQL主题失败: dbId=" + dbId + ", modelDb=" + modelDb, e);
            return Result.fail("获取MySQL主题失败: " + e.getMessage());
        }
    }

    /**
     * 导入MySQL模型库主题到主题库（按主题名去重更新，字段结构一并写入）。
     * body: {dbId, modelDb, addPrefix?, stripPrefix?, topicNames?: string[]}，topicNames 为 MySQL 原始主题名，空=全部。
     */
    @PostMapping("/topic/mysql/import")
    public Result<?> importMysqlTopics(@RequestBody Map<String, Object> body) {
        try {
            Long dbId = null;
            Object dbIdObj = body.get("dbId");
            if (dbIdObj instanceof Number) {
                dbId = ((Number) dbIdObj).longValue();
            } else if (dbIdObj != null) {
                dbId = Long.parseLong(dbIdObj.toString());
            }
            String modelDb = body.get("modelDb") != null ? body.get("modelDb").toString() : null;
            boolean addPrefix = !Boolean.FALSE.equals(body.get("addPrefix"));
            boolean stripPrefix = !Boolean.FALSE.equals(body.get("stripPrefix"));
            @SuppressWarnings("unchecked")
            List<String> topicNames = (List<String>) body.get("topicNames");
            return Result.ok(topicImportService.importTopics(dbId, modelDb, addPrefix, stripPrefix, topicNames));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("导入MySQL主题失败: {}", body, e);
            return Result.fail("导入MySQL主题失败: " + e.getMessage());
        }
    }

    // ========== 数据采集配置 CRUD ==========

    @GetMapping("/cjConfigs")
    public Result<List<CjConfig>> getCjConfigs() {
        return Result.ok(cjConfigMapper.selectList(null));
    }

    // ========== 处理流程列表 ==========

    @GetMapping("/transformations")
    public Result<List<Transformation>> getTransformations() {
        return Result.ok(transformationMapper.selectList(null));
    }

    @GetMapping("/cj/page")
    public Result<?> getCjPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String ybName) {
        Page<CjConfig> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<CjConfig> wrapper = new LambdaQueryWrapper<>();
        if (ybName != null && !ybName.isEmpty()) {
            wrapper.like(CjConfig::getYbName, ybName);
        }
        wrapper.orderByDesc(CjConfig::getCreateTime);
        page = cjConfigMapper.selectPage(page, wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @PostMapping("/cj/save")
    public Result<?> saveCj(@RequestBody CjConfig cj) {
        if (cj.getId() != null) {
            cjConfigMapper.updateById(cj);
        } else {
            cjConfigMapper.insert(cj);
        }
        return Result.ok();
    }

    @PostMapping("/cj/delete")
    public Result<?> deleteCj(@RequestParam Long id) {
        cjConfigMapper.deleteById(id);
        return Result.ok();
    }

    // ========== 仿真方案 CRUD ==========

    @GetMapping("/page")
    public Result<?> getSchemePage(
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String schemeName) {
        Page<Scheme> page = schemeService.getSchemePage(pageIndex, pageSize, schemeName);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @GetMapping("/detail")
    public Result<Scheme> getSchemeById(@RequestParam Long id) {
        return Result.ok(schemeService.getSchemeById(id));
    }

    @GetMapping("/detailData")
    public Result<SchemeDetailDataVO> getSchemeDetailData(
            @RequestParam Long schemeId,
            @RequestParam(required = false) String ybList,
            @RequestParam(required = false) String equipId) {
        SchemeDetailDataVO data = schemeService.getSchemeDetailData(schemeId, ybList, equipId);
        if (data == null) {
            return Result.fail("方案不存在");
        }
        return Result.ok(data);
    }

    @GetMapping("/schemeDetails")
    public Result<List<SchemeDetail>> getSchemeDetails(@RequestParam Long schemeId) {
        return Result.ok(schemeService.getSchemeDetails(schemeId));
    }

    @GetMapping("/equipTree")
    public Result<List<SchemeEquipVO>> getEquipTree(@RequestParam String schemeId) {
        return Result.ok(schemeService.getSchemeEquipTree(schemeId));
    }

    @PostMapping("/save")
    public Result<?> saveScheme(@RequestBody Scheme scheme) {
        schemeService.saveSchemeAndDetail(scheme);
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result<?> deleteScheme(@RequestParam Long id) {
        schemeService.deleteSchemeAndDetail(id);
        return Result.ok();
    }

    // ========== 样本三级联动 ==========

    // ========== 仿真方案（本地） ==========

    @GetMapping("/sample/plans")
    public Result<?> getSamplePlans() {
        return Result.ok(sampleCascadeService.getAllPlans());
    }

    // ========== 从MySQL数据库查仿真方案（实验设计） ==========

    /**
     * 从 MySQL 查询 big_sample_db.experiment_design 表，
     * 按 ctime 排序，返回 [{id, planName}] 供仿真方案下拉使用。
     */
    @GetMapping("/experimentDesign/list")
    public Result<?> getExperimentDesignList(
            @RequestParam Long dbId,
            @RequestParam(defaultValue = "big_sample_db") String dbName) {
        try {
            DatabaseSource ds = databaseSourceMapper.selectById(dbId);
            if (ds == null) return Result.fail("数据源不存在: " + dbId);
            if (!"MYSQL".equalsIgnoreCase(ds.getDbType())) return Result.fail("仅支持MySQL数据源");

            int port = ds.getPort() != null ? ds.getPort() : 3306;
            String url = "jdbc:mysql://" + ds.getHost() + ":" + port + "/" + dbName
                    + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
            java.util.List<Map<String, Object>> list = new ArrayList<>();
            java.sql.Connection conn = java.sql.DriverManager.getConnection(url, ds.getUsername(), ds.getPassword());
            try {
                java.sql.PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, experiment_design_name FROM experiment_design ORDER BY ctime DESC");
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getString("id"));
                    row.put("planName", rs.getString("experiment_design_name"));
                    list.add(row);
                }
                rs.close();
                ps.close();
            } finally {
                conn.close();
            }
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询实验设计失败: dbId=" + dbId + ", dbName=" + dbName, e);
            return Result.fail("查询实验设计失败: " + e.getMessage());
        }
    }

    /**
     * 根据 experiment_design_id 查询 big_sample 表，返回 [{id, dataName}]
     */
    @GetMapping("/bigSample/list")
    public Result<?> getBigSampleList(
            @RequestParam Long dbId,
            @RequestParam(defaultValue = "big_sample_db") String dbName,
            @RequestParam String experimentDesignId) {
        try {
            DatabaseSource ds = databaseSourceMapper.selectById(dbId);
            if (ds == null) return Result.fail("数据源不存在: " + dbId);
            if (!"MYSQL".equalsIgnoreCase(ds.getDbType())) return Result.fail("仅支持MySQL数据源");

            int port = ds.getPort() != null ? ds.getPort() : 3306;
            String jdbcUrl = "jdbc:mysql://" + ds.getHost() + ":" + port + "/" + dbName
                    + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
            java.util.List<Map<String, Object>> list = new ArrayList<>();
            java.sql.Connection conn = java.sql.DriverManager.getConnection(jdbcUrl, ds.getUsername(), ds.getPassword());
            try {
                java.sql.PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, name FROM big_sample WHERE experiment_design_id = ? ORDER BY id");
                ps.setString(1, experimentDesignId);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getString("id"));
                    row.put("dataName", rs.getString("name"));
                    list.add(row);
                }
                rs.close();
                ps.close();
            } finally {
                conn.close();
            }
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询大样本失败: dbId=" + dbId + ", designId=" + experimentDesignId, e);
            return Result.fail("查询大样本失败: " + e.getMessage());
        }
    }

    /**
     * 根据 deduce_config_id（即 big_sample.id）查询 task_design_example 表
     * 返回 [{id, dataName}]（id = task_design_example.id，dataName = task_design_example.name）
     */
    @GetMapping("/taskDesignExample/list")
    public Result<?> getTaskDesignExampleList(
            @RequestParam Long dbId,
            @RequestParam(defaultValue = "sim_control_db_copy") String dbName,
            @RequestParam String deduceConfigId) {
        try {
            DatabaseSource ds = databaseSourceMapper.selectById(dbId);
            if (ds == null) return Result.fail("数据源不存在: " + dbId);
            if (!"MYSQL".equalsIgnoreCase(ds.getDbType())) return Result.fail("仅支持MySQL数据源");

            int port = ds.getPort() != null ? ds.getPort() : 3306;
            String jdbcUrl = "jdbc:mysql://" + ds.getHost() + ":" + port + "/" + dbName
                    + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
            java.util.List<Map<String, Object>> list = new ArrayList<>();
            java.sql.Connection conn = java.sql.DriverManager.getConnection(jdbcUrl, ds.getUsername(), ds.getPassword());
            try {
                java.sql.PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, name FROM task_design_example WHERE deduce_config_id = ? ORDER BY id");
                ps.setString(1, deduceConfigId);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                        row.put("id", rs.getString("id"));
                    row.put("subName", rs.getString("name"));
                    list.add(row);
                }
                rs.close();
                ps.close();
            } finally {
                conn.close();
            }
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询样本子集失败: dbId=" + dbId + ", deduceConfigId=" + deduceConfigId, e);
            return Result.fail("查询样本子集失败: " + e.getMessage());
        }
    }

    /**
     * 根据 task_design_example_id 查询 task_design_example_id 表
     * 返回 [{id, dataName, deduceTaskId}]（id = 表主键，dataName = deduce_task_id，deduceTaskId = deduce_task_id）
     */
    @GetMapping("/deduceTask/list")
    public Result<?> getDeduceTaskList(
            @RequestParam Long dbId,
            @RequestParam(defaultValue = "sim_control_db_copy") String dbName,
            @RequestParam String taskDesignExampleId) {
        try {
            DatabaseSource ds = databaseSourceMapper.selectById(dbId);
            if (ds == null) return Result.fail("数据源不存在: " + dbId);
            if (!"MYSQL".equalsIgnoreCase(ds.getDbType())) return Result.fail("仅支持MySQL数据源");

            int port = ds.getPort() != null ? ds.getPort() : 3306;
            String jdbcUrl = "jdbc:mysql://" + ds.getHost() + ":" + port + "/" + dbName
                    + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
            java.util.List<Map<String, Object>> list = new ArrayList<>();
            java.sql.Connection conn = java.sql.DriverManager.getConnection(jdbcUrl, ds.getUsername(), ds.getPassword());
            try {
                java.sql.PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, deduce_task_id FROM task_example_correlation WHERE task_design_example_id = ? ORDER BY id");
                ps.setString(1, taskDesignExampleId);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getString("id"));
                    row.put("dataName", rs.getString("deduce_task_id"));
                    list.add(row);
                }
                rs.close();
                ps.close();
            } finally {
                conn.close();
            }
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询实例失败: dbId=" + dbId + ", taskDesignExampleId=" + taskDesignExampleId, e);
            return Result.fail("查询实例失败: " + e.getMessage());
        }
    }

    @GetMapping("/sample/data")
    public Result<List<SampleData>> getSampleDataByPlanId(@RequestParam Long planId) {
        return Result.ok(sampleCascadeService.getDataByPlanId(planId));
    }

    @GetMapping("/sample/subData")
    public Result<List<SampleSubData>> getSampleSubDataByDataId(@RequestParam Long dataId) {
        return Result.ok(sampleCascadeService.getSubDataByDataId(dataId));
    }

    @GetMapping("/sample/detail")
    public Result<List<SampleDetail>> getSampleDetailBySubDataId(@RequestParam Long subDataId) {
        return Result.ok(sampleCascadeService.getDetailBySubDataId(subDataId));
    }

    // ========== 详细数据查询 ==========

    /**
     * 查询某个实例的主题数据（分页）。
     * 返回 [{topicName, columns:[{name,alias}], rows:[[val,...]], total:N}]
     */
    @GetMapping("/topicData")
    public Result<?> getTopicData(@RequestParam Long schemeId, @RequestParam Long instanceId,
                                  @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                  @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                  @RequestParam(required = false) Integer topicIndex) {
        try {
            Scheme scheme = schemeMapper.selectById(schemeId);
            if (scheme == null || scheme.getCjListId() == null) {
                return Result.fail("方案或采集配置不存在");
            }
            CjConfig cjConfig = cjConfigMapper.selectById(scheme.getCjListId());
            if (cjConfig == null || cjConfig.getYbList() == null || cjConfig.getYbList().isEmpty()) {
                return Result.fail("采集配置的YB_LIST为空");
            }

            List<Long> topicIds = new ArrayList<>();
            for (String s : cjConfig.getYbList().split(",")) {
                s = s.trim();
                if (!s.isEmpty()) {
                    try { topicIds.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
                }
            }
            if (topicIds.isEmpty()) return Result.ok(new ArrayList<>());

            List<TopicConfig> topics = topicConfigMapper.selectBatchIds(topicIds);
            List<Map<String, Object>> result = new ArrayList<>();

            DataSource dmDataSource = schemeProcessService.getDmDataSource();
            try (Connection conn = dmDataSource.getConnection()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET SCHEMA " + dmSchema);
                }
                int topicIdx = 0;
                for (TopicConfig topic : topics) {
                    // 如果指定了 topicIndex，只处理该索引的主题
                    if (topicIndex != null && topicIdx != topicIndex) {
                        topicIdx++;
                        continue;
                    }
                    topicIdx++;
                    String tableName = topic.getTableName();
                    if (tableName == null || tableName.isEmpty()) continue;
                    List<TopicField> fields = topicFieldMapper.selectList(
                            new LambdaQueryWrapper<TopicField>()
                                    .eq(TopicField::getTopicId, topic.getId())
                                    .orderByAsc(TopicField::getSortOrder));
                    if (fields.isEmpty()) continue;

                    Map<String, Object> topicData = new HashMap<>();
                    topicData.put("topicName", topic.getTopicName());
                    topicData.put("topicCode", topic.getTopicCode());

                    // 列定义
                    List<Map<String, String>> columns = new ArrayList<>();
                    for (TopicField f : fields) {
                        Map<String, String> col = new HashMap<>();
                        col.put("name", f.getFieldName());
                        col.put("alias", f.getFieldAlias() != null ? f.getFieldAlias() : f.getFieldName());
                        columns.add(col);
                    }
                    topicData.put("columns", columns);

                    String colNames = columns.stream().map(c -> c.get("name")).reduce((a, b) -> a + ", " + b).orElse("");
                    String baseSql = " FROM " + tableName + " WHERE SCHEME_ID = " + instanceId;

                    // 先查总数
                    int total = 0;
                    try (PreparedStatement countPs = conn.prepareStatement("SELECT COUNT(*)" + baseSql)) {
                        try (ResultSet rs = countPs.executeQuery()) {
                            if (rs.next()) total = rs.getInt(1);
                        }
                    }
                    topicData.put("total", total);

                    // 分页查数据
                    int offset = (pageIndex - 1) * pageSize;
                    List<List<Object>> rows = new ArrayList<>();
                    String querySql = "SELECT " + colNames + baseSql + " ORDER BY ROWNUM OFFSET " + offset + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
                    try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                List<Object> row = new ArrayList<>();
                                for (Map<String, String> col : columns) {
                                    row.add(rs.getObject(col.get("name")));
                                }
                                rows.add(row);
                            }
                        }
                    }
                    topicData.put("rows", rows);
                    result.add(topicData);
                }
            }
            return Result.ok(result);
        } catch (Exception e) {
            log.error("查询主题数据失败: schemeId=" + schemeId + ", instanceId=" + instanceId, e);
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    // ========== 引接处理 ==========

    @PostMapping("/process")
    public Result<?> processScheme(@RequestParam Long schemeId,
                                   @RequestParam(required = false, defaultValue = "false") boolean reprocess) {
        SchemeProcessService.ProcessProgress progress = schemeProcessService.getProgress(schemeId);
        if (progress != null && progress.isRunning()) {
            return Result.fail("该方案正在处理中");
        }
        // 重新引接：先删除之前写入的数据，再重置状态
        if (reprocess) {
            schemeProcessService.deleteExistingData(schemeId);
        }
        schemeProcessService.initProgress(schemeId);
        schemeProcessService.processScheme(schemeId);
        return Result.ok();
    }

    @GetMapping("/progress")
    public Result<?> getProgress(@RequestParam Long schemeId) {
        SchemeProcessService.ProcessProgress progress = schemeProcessService.getProgress(schemeId);
        Map<String, Object> result = new HashMap<>();
        if (progress == null) {
            result.put("total", 0);
            result.put("completed", 0);
            result.put("finished", true);
            result.put("running", false);
        } else {
            result.put("total", progress.getTotal());
            result.put("completed", progress.getCompleted());
            result.put("finished", progress.isFinished());
            result.put("running", progress.isRunning());
        }
        System.out.println("getProgress: schemeId=" + schemeId + ", result=" + result);
        return Result.ok(result);
    }
}
