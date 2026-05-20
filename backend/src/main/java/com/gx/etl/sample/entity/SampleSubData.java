package com.gx.etl.sample.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_SAMPLE_SUB_DATA")
public class SampleSubData {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("DATA_ID")
    private Long dataId;

    @TableField("SUB_NAME")
    private String subName;

    @TableField("SUB_DESC")
    private String subDesc;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
