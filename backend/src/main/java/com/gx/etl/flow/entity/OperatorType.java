package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("T_OPERATOR_TYPE")
public class OperatorType {

    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

    private String pluginId;

    private String categoryId;

    private String icon;

    private String dragIcon;

    private String description;

    private String implementation;

    private String dom;

    private Integer sortOrder;

    private Integer type;

    private Integer status;
}
