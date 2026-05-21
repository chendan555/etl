package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_TABLE_RELATION")
public class YjTableRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configName;

    private String taskId;

    private String taskName;

    private String nodeId;

    private String nodeName;

    private Long dataSourceId;

    private String databaseName;

    private String schemaId;

    @TableField("\"SCHEMA\"")
    private String schema;

    private String mainTableName;

    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.CLOB)
    private String jsonForm;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
