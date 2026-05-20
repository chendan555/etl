package com.gx.etl.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_CJ_CONFIG")
public class CjConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("YB_NAME")
    private String ybName;

    @TableField("YB_LIST")
    private String ybList;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
