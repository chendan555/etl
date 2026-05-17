package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_TRANSFORMATION")
public class Transformation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 前端使用的ID字段别名 */
    @TableField(exist = false)
    private Long transformationId;

    private String name;

    private Long categoriesId;

    private String flowJson;

    private String imgUrl;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 目录路径（非数据库字段） */
    @TableField(exist = false)
    private String directoryName;

    /** 所属任务组ID（非数据库字段） */
    @TableField(exist = false)
    private String belongToTaskGroupId;

    /** 所属任务组名称（非数据库字段） */
    @TableField(exist = false)
    private String belongToTaskGroup;
}
