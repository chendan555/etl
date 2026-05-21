package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjHistory;
import com.gx.etl.receive.mapper.YjHistoryMapper;
import org.springframework.stereotype.Service;

@Service
public class YjHistoryService extends ServiceImpl<YjHistoryMapper, YjHistory> {

    public Page<YjHistory> listPage(int pageIndex, int pageSize, String mainBody) {
        return listPage(pageIndex, pageSize, mainBody, null, null, null);
    }

    public Page<YjHistory> listPage(int pageIndex, int pageSize, String mainBody,
                                     String batch, Long configId, String toTable) {
        Page<YjHistory> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjHistory> wrapper = new LambdaQueryWrapper<>();
        if (mainBody != null && !mainBody.isEmpty()) {
            wrapper.eq(YjHistory::getMainBody, mainBody);
        }
        if (batch != null && !batch.isEmpty()) {
            wrapper.eq(YjHistory::getBatch, batch);
        }
        if (configId != null) {
            wrapper.eq(YjHistory::getConfigId, configId);
        }
        if (toTable != null && !toTable.isEmpty()) {
            wrapper.like(YjHistory::getToTable, toTable);
        }
        wrapper.orderByDesc(YjHistory::getCreateTime);
        return page(page, wrapper);
    }
}
