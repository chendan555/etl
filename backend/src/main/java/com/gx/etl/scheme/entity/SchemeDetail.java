package com.gx.etl.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_SCHEME_DETAIL")
public class SchemeDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("SCHEME_ID")
    private Long schemeId;

    @TableField("YB_ID")
    private Long ybId;

    @TableField("YB_NAME")
    private String ybName;

    @TableField("YB_LIST")
    private String ybList;

    @TableField("STATUS")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
