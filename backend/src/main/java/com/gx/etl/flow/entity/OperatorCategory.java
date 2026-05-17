package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("T_OPERATOR_CATEGORY")
public class OperatorCategory {

    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

    private String icon;

    private Integer sortOrder;

    private Integer type;
}
