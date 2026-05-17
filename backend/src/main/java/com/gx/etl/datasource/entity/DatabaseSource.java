package com.gx.etl.datasource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_DATABASE")
public class DatabaseSource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String dbType;

    private String host;

    private Integer port;

    private String databaseName;

    private String username;

    private String password;

    private String sid;

    private String schemaFilter;

    private String isInclude;

    private Long categoriesId;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
