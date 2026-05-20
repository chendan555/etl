package com.gx.etl.sample.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_SAMPLE_PLAN")
public class SamplePlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("PLAN_NAME")
    private String planName;

    @TableField("PLAN_DESC")
    private String planDesc;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
