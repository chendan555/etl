package com.gx.etl.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("T_PLAN_DETAIL")
public class PlanDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("PLAN_ID")
    private Long planId;

    @TableField("SOURCE_ID")
    private String sourceId;

    @TableField("DETAIL_TYPE")
    private String detailType;

    @TableField("KETTLE_NAME")
    private String kettleName;

    @TableField("GROUP_NAME")
    private String groupName;

    @TableField("TYPE")
    private String type;

    @TableField("REMARK")
    private String remark;

    @TableField("AGREEMENT_NAME")
    private String agreementName;

    @TableField("SORT_ORDER")
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
