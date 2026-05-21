package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_TABLE_CONFIG")
public class YjTableConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tableName;

    private Long dataSourceId;

    private String dataSourceName;

    @TableField("\"SCHEMA\"")
    private String schema;

    private Long tableConfigId;

    private String tableConfigName;

    private Long targetDataSourceId;

    private String targetDataSourceName;

    private String targetSchema;

    private String remarks;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
