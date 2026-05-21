package com.gx.etl.receive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_YJ_FILE_CONFIG")
public class YjFileConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String fileMulti;

    private String filePath;

    private String folderPath;

    private Long analysisProtocolId;

    private String analysisProtocolName;

    private Long targetDataSourceId;

    private String targetDataSourceName;

    private String targetSchema;

    private Integer taskDataFlag;

    private String jumpRow;

    private String remark;

    private String pathType;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
