package com.gx.etl.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gx.etl.scheme.entity.Scheme;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchemeMapper extends BaseMapper<Scheme> {
}
