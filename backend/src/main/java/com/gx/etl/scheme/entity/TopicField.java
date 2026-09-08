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

    /** 字段数据类型（string/long/double 等，对应 MySQL dataitem.type） */
    @TableField("FIELD_TYPE")
    private String fieldType;

    /** 是否为嵌套对象字段（Y/N，对应 MySQL dataitem.instruction_type） */
    @TableField("COMPLEX_TYPE")
    private String complexType;

    /** 字段值在 msgpack 根数组中的下标（含 complexType 子字段：指向父字段所在嵌套数组位置） */
    @TableField("ARRAY_INDEX")
    private Integer arrayIndex;

    /** complexType 子字段在父嵌套数组内的下标（simpleType 为 null） */
    @TableField("SUB_INDEX")
    private Integer subIndex;
}
