package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_EXECUTION_LOG")
public class ExecutionLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long transId;

    private String transName;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String errorMsg;

    private Long rowCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
