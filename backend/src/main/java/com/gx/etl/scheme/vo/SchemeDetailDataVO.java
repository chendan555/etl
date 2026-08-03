package com.gx.etl.scheme.vo;

import lombok.Data;

import java.util.List;

@Data
public class SchemeDetailDataVO {
    private Long schemeId;
    private String schemeName;
    private Integer status;
    private String createTime;
    private EquipmentInfoVO info;
}
