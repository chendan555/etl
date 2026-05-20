package com.gx.etl.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("T_SCHEME")
public class Scheme {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("SCHEME_NAME")
    private String schemeName;

    @TableField("SCHEME_DESC")
    private String schemeDesc;

    @TableField("CJ_LIST_ID")
    private Long cjListId;

    @TableField("TRANS_ID")
    private Long transId;

    @TableField("STATUS")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 子表数据（非数据库字段） */
    @TableField(exist = false)
    private List<SchemeDetail> detailList;

    /** 数据采集配置名称（非数据库字段） */
    @TableField(exist = false)
    private String cjName;

    /** 处理流程名称（非数据库字段） */
    @TableField(exist = false)
    private String transName;
}
