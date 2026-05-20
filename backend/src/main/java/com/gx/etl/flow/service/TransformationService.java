package com.gx.etl.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.flow.entity.ExecutionLog;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.mapper.ExecutionLogMapper;
import com.gx.etl.flow.mapper.TransformationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransformationService extends ServiceImpl<TransformationMapper, Transformation> {

    @Resource
    private ExecutionLogMapper executionLogMapper;

    public Page<Transformation> listPage(int pageIndex, int pageSize, Long categoriesId) {
        Page<Transformation> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<Transformation> wrapper = new LambdaQueryWrapper<>();
        if (categoriesId != null) {
            wrapper.eq(Transformation::getCategoriesId, categoriesId);
        }
        wrapper.orderByDesc(Transformation::getCreateTime);
        Page<Transformation> result = page(page, wrapper);
        // 设置前端需要的字段
        for (Transformation trans : result.getRecords()) {
            trans.setTransformationId(trans.getId());
            trans.setDirectoryName("transformation/" + trans.getId());
            if (trans.getCategoriesId() != null) {
                trans.setBelongToTaskGroupId(trans.getCategoriesId().toString());
            }
        }
        return result;
    }

    public Long saveTransformation(String name, Long categoriesId, String flowJson, String imgUrl, String remark) {
        return saveTransformation(name, categoriesId, flowJson, imgUrl, remark, null);
    }

    public Long saveTransformation(String name, Long categoriesId, String flowJson, String imgUrl, String remark, String bindTableNames) {
        Transformation trans = new Transformation();
        trans.setName(name);
        trans.setCategoriesId(categoriesId);
        trans.setFlowJson(flowJson);
        trans.setImgUrl(imgUrl);
        trans.setRemark(remark);
        trans.setBindTableNames(bindTableNames);
        trans.setStatus(0);
        trans.setCreateTime(LocalDateTime.now());
        trans.setUpdateTime(LocalDateTime.now());
        save(trans);
        return trans.getId();
    }

    public void updateTransformation(Long id, String name, String flowJson, String imgUrl, String remark) {
        updateTransformation(id, name, flowJson, imgUrl, remark, null);
    }

    public void updateTransformation(Long id, String name, String flowJson, String imgUrl, String remark, String bindTableNames) {
        Transformation trans = getById(id);
        if (trans != null) {
            if (name != null) trans.setName(name);
            if (flowJson != null) trans.setFlowJson(flowJson);
            if (imgUrl != null) trans.setImgUrl(imgUrl);
            if (remark != null) trans.setRemark(remark);
            if (bindTableNames != null) trans.setBindTableNames(bindTableNames);
            trans.setUpdateTime(LocalDateTime.now());
            updateById(trans);
        }
    }

    public ExecutionLog createExecutionLog(Long transId, String transName) {
        ExecutionLog log = new ExecutionLog();
        log.setTransId(transId);
        log.setTransName(transName);
        log.setStatus(0);
        log.setStartTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        executionLogMapper.insert(log);
        return log;
    }

    public void updateExecutionLog(ExecutionLog log, int status, String errorMsg, long rowCount) {
        log.setStatus(status);
        log.setEndTime(LocalDateTime.now());
        log.setErrorMsg(errorMsg);
        log.setRowCount(rowCount);
        executionLogMapper.updateById(log);
    }

    public List<ExecutionLog> getExecutionLogs(Long transId) {
        return executionLogMapper.selectList(
                new LambdaQueryWrapper<ExecutionLog>()
                        .eq(ExecutionLog::getTransId, transId)
                        .orderByDesc(ExecutionLog::getCreateTime));
    }

    public Page<ExecutionLog> getExecutionLogPage(Page<ExecutionLog> page, LambdaQueryWrapper<ExecutionLog> wrapper) {
        return executionLogMapper.selectPage(page, wrapper);
    }
}
