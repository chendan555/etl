package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjSimulationConfig;
import com.gx.etl.receive.mapper.YjSimulationConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class YjSimulationConfigService extends ServiceImpl<YjSimulationConfigMapper, YjSimulationConfig> {

    public Page<YjSimulationConfig> listPage(int pageIndex, int pageSize) {
        Page<YjSimulationConfig> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjSimulationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(YjSimulationConfig::getCreateTime);
        return page(page, wrapper);
    }
}
