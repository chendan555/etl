package com.gx.etl.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@TableName("T_SCHEME_EQUIP")
public class SchemeEquip {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("SCHEME_ID")
    private String schemeId;

    @TableField("EQUIP_ID")
    private String equipId;

    @TableField("EQUIP_NAME")
    private String equipName;

    @TableField("PID")
    private String pid;

    /** 子节点列表（非数据库字段） */
    @TableField(exist = false)
    private List<SchemeEquip> children;
}
