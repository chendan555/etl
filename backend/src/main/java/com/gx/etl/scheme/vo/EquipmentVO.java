package com.gx.etl.scheme.vo;

import lombok.Data;

@Data
public class EquipmentVO {
    private Long id;
    private String name;
    private String type;
    private EquipmentInfoVO info;
}
