package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("T_TASK_GROUP")
public class TaskGroup {

    @TableId(value = "ID_TASK_GROUP", type = IdType.AUTO)
    private Long idTaskGroup;

    @TableField("TASK_GROUP_NAME")
    private String taskGroupName;

    @TableField("TASK_GROUP_CODE")
    private String taskGroupCode;

    @TableField("TASK_DESC")
    private String taskDesc;

    @TableField("TYPE")
    private String type;

    @TableField("PARENT_ID")
    private Long parentId;

    @TableField("IS_ROOT")
    private Integer isRoot;

    @TableField("COUNT_TRANS")
    private Integer countTrans;

    @TableField("SORT_ORDER")
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 子节点列表（非数据库字段） */
    @TableField(exist = false)
    private List<TaskGroup> children;

    /** 是否禁用（非数据库字段） */
    @TableField(exist = false)
    private Boolean disabled;

    /** 创建日期字符串（非数据库字段） */
    @TableField(exist = false)
    private String createDate;

    /** 更新日期字符串（非数据库字段） */
    @TableField(exist = false)
    private String updateDate;
}
