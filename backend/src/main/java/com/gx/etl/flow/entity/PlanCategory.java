package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("T_PLAN_CATEGORY")
public class PlanCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("NAME")
    private String name;

    @TableField("DESCS")
    private String descs;

    @TableField("PARENT_ID")
    private Long parentId;

    @TableField("SORT_ORDER")
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 子节点列表（非数据库字段） */
    @TableField(exist = false)
    private List<PlanCategory> children;
}
