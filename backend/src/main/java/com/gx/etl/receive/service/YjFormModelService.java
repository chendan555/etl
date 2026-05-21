package com.gx.etl.receive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.receive.entity.YjFormModel;
import com.gx.etl.receive.mapper.YjFormModelMapper;
import org.springframework.stereotype.Service;

@Service
public class YjFormModelService extends ServiceImpl<YjFormModelMapper, YjFormModel> {

    public Page<YjFormModel> listPage(int pageIndex, int pageSize) {
        Page<YjFormModel> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<YjFormModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(YjFormModel::getCreateTime);
        return page(page, wrapper);
    }
}
