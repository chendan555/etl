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
import com.gx.etl.scheme.mapper.TopicConfigMapper;
import com.gx.etl.scheme.mapper.TopicFieldMapper;
import com.gx.etl.scheme.service.SchemeService;
import com.gx.etl.scheme.vo.SchemeDetailDataVO;
import com.gx.etl.scheme.service.SchemeProcessService;
import com.gx.etl.sample.service.SampleCascadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheme")
public class SchemeController {

    private static final Logger log = LoggerFactory.getLogger(SchemeController.class);

    @Resource
    private SchemeService schemeService;

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
            @RequestParam(required = false) String ybList) {
        SchemeDetailDataVO data = schemeService.getSchemeDetailData(schemeId, ybList);
        if (data == null) {
            return Result.fail("方案不存在");
        }
        return Result.ok(data);
    }

    @GetMapping("/schemeDetails")
    public Result<List<SchemeDetail>> getSchemeDetails(@RequestParam Long schemeId) {
        return Result.ok(schemeService.getSchemeDetails(schemeId));
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

    @GetMapping("/sample/plans")
    public Result<List<SamplePlan>> getSamplePlans() {
        return Result.ok(sampleCascadeService.getAllPlans());
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

    // ========== 引接处理 ==========

    @PostMapping("/process")
    public Result<?> processScheme(@RequestParam Long schemeId) {
        SchemeProcessService.ProcessProgress progress = schemeProcessService.getProgress(schemeId);
        if (progress != null && progress.isRunning()) {
            return Result.fail("该方案正在处理中");
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
