package com.gx.etl.sample.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_SAMPLE_DATA")
public class SampleData {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("PLAN_ID")
    private Long planId;

    @TableField("DATA_NAME")
    private String dataName;

    @TableField("DATA_DESC")
    private String dataDesc;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
