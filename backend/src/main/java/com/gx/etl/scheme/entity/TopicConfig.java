package com.gx.etl.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("T_TOPIC_CONFIG")
public class TopicConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("TOPIC_NAME")
    private String topicName;

    @TableField("TOPIC_CODE")
    private String topicCode;

    @TableField("TABLE_NAME")
    private String tableName;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 字段列表（非数据库字段） */
    @TableField(exist = false)
    private List<TopicField> fieldList;
}
