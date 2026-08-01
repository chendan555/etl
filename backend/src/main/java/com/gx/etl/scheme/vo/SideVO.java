package com.gx.etl.scheme.vo;

import lombok.Data;

import java.util.List;

@Data
public class SideVO {
    private String side;
    private String name;
    private String color;
    private List<EquipmentVO> equipmentList;
}
