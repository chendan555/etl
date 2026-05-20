package com.gx.etl.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gx.etl.sample.entity.SampleData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SampleDataMapper extends BaseMapper<SampleData> {
}
