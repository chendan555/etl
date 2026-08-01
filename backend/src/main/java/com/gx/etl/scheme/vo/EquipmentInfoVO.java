package com.gx.etl.scheme.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EquipmentInfoVO {
    private List<Map<String, Object>> basic;
    private List<Map<String, Object>> status;
    private List<Map<String, Object>> combat;
    private List<Map<String, Object>> records;
}
