package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_FORM_MODEL")
public class YjFormModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String formName;

    private String tableName;

    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.CLOB)
    private String allConfig;

    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.CLOB)
    private String formModel;

    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
