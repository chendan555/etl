package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_PROTOCOL")
public class YjProtocol {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String tableName;

    private String fileType;

    private String fileTypeName;

    private String separator;

    private String separatorName;

    private String tableIdType;

    private String idColumnName;

    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.CLOB)
    private String jsonForm;

    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.CLOB)
    private String tableId;

    private String remark;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
