package com.gx.etl.flow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gx.etl.flow.entity.TaskGroup;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskGroupMapper extends BaseMapper<TaskGroup> {
}
