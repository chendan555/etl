package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_HISTORY")
public class YjHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String batch;

    private String mainBody;

    private Long configId;

    private Long toDataSourceId;

    private String toDataSourceName;

    private String toSchema;

    private String toTable;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
