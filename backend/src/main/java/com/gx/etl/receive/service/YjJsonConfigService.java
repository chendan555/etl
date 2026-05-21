package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjJsonConfig;
import com.gx.etl.receive.mapper.YjJsonConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class YjJsonConfigService extends ServiceImpl<YjJsonConfigMapper, YjJsonConfig> {

    public YjJsonConfig getByHistoryId(Long historyId) {
        LambdaQueryWrapper<YjJsonConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YjJsonConfig::getHistoryId, historyId);
        return getOne(wrapper);
    }
}
