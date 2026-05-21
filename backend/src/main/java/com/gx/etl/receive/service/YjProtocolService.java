package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjProtocol;
import com.gx.etl.receive.mapper.YjProtocolMapper;
import org.springframework.stereotype.Service;

@Service
public class YjProtocolService extends ServiceImpl<YjProtocolMapper, YjProtocol> {

    public Page<YjProtocol> listPage(int pageIndex, int pageSize, String configName) {
        Page<YjProtocol> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjProtocol> wrapper = new LambdaQueryWrapper<>();
        if (configName != null && !configName.isEmpty()) {
            wrapper.like(YjProtocol::getName, configName);
        }
        wrapper.orderByDesc(YjProtocol::getCreateDate);
        return page(page, wrapper);
    }
}
