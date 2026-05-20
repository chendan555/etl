package com.gx.etl.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("T_TOPIC_FIELD")
public class TopicField {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("TOPIC_ID")
    private Long topicId;

    @TableField("FIELD_NAME")
    private String fieldName;

    @TableField("FIELD_ALIAS")
    private String fieldAlias;

    @TableField("SORT_ORDER")
    private Integer sortOrder;
}
