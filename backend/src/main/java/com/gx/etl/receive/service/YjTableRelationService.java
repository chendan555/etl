package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjTableRelation;
import com.gx.etl.receive.mapper.YjTableRelationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YjTableRelationService extends ServiceImpl<YjTableRelationMapper, YjTableRelation> {

    public Page<YjTableRelation> listPage(int pageIndex, int pageSize, String configName) {
        Page<YjTableRelation> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjTableRelation> wrapper = new LambdaQueryWrapper<>();
        if (configName != null && !configName.isEmpty()) {
            wrapper.like(YjTableRelation::getConfigName, configName);
        }
        wrapper.orderByDesc(YjTableRelation::getCreateTime);
        return page(page, wrapper);
    }

    public List<YjTableRelation> listAll() {
        LambdaQueryWrapper<YjTableRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(YjTableRelation::getCreateTime);
        return list(wrapper);
    }
}
