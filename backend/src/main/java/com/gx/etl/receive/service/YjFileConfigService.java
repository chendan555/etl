package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjFileConfig;
import com.gx.etl.receive.mapper.YjFileConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class YjFileConfigService extends ServiceImpl<YjFileConfigMapper, YjFileConfig> {

    public Page<YjFileConfig> listPage(int pageIndex, int pageSize, String name) {
        Page<YjFileConfig> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjFileConfig> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(YjFileConfig::getName, name);
        }
        wrapper.orderByDesc(YjFileConfig::getCreateTime);
        return page(page, wrapper);
    }
}
