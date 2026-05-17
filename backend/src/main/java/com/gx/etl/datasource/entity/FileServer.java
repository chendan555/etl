package com.gx.etl.datasource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_FILE_SERVER")
public class FileServer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String serverType;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String basePath;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
