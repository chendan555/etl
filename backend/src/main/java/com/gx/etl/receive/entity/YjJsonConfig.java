package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_JSON_CONFIG")
public class YjJsonConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long historyId;

    private Long dataSourceId;

    @TableField("\"SCHEMA\"")
    private String schema;

    private String tableName;

    private String columnName;

    private String instanceId;

    private String modelCode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
