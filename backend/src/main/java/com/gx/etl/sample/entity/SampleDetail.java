package com.gx.etl.sample.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_SAMPLE_DETAIL")
public class SampleDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("SUB_DATA_ID")
    private Long subDataId;

    @TableField("ITEM_NAME")
    private String itemName;

    @TableField("ITEM_DESC")
    private String itemDesc;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
