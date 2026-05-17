package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("T_TRANS_PLAN")
public class TransPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("NAME")
    private String name;

    @TableField("GROUP_ID")
    private Long groupId;

    @TableField("STATUS")
    private String status;

    @TableField("FLAG")
    private Integer flag;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 处理流程列表（非数据库字段） */
    @TableField(exist = false)
    private List<PlanDetail> clList;

    /** 引接列表（非数据库字段） */
    @TableField(exist = false)
    private List<PlanDetail> yjList;

    /** 状态信息（非数据库字段） */
    @TableField(exist = false)
    private String statusInfo;

    /** 创建日期字符串（非数据库字段） */
    @TableField(exist = false)
    private String createdDate;
}
