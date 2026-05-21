package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjTableConfig;
import com.gx.etl.receive.mapper.YjTableConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class YjTableConfigService extends ServiceImpl<YjTableConfigMapper, YjTableConfig> {

    public Page<YjTableConfig> listPage(int pageIndex, int pageSize, String tableName) {
        Page<YjTableConfig> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjTableConfig> wrapper = new LambdaQueryWrapper<>();
        if (tableName != null && !tableName.isEmpty()) {
            wrapper.like(YjTableConfig::getTableName, tableName);
        }
        wrapper.orderByDesc(YjTableConfig::getCreateTime);
        return page(page, wrapper);
    }
}
