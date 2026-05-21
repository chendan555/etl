package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_EXEC_LOG")
public class YjExecLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String batch;

    private Long configId;

    private String configType;

    private Integer execStatus;

    private Integer execTime;

    private Long rowCount;

    private String errorMsg;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
