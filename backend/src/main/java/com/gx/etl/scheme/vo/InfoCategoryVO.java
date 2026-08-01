package com.gx.etl.scheme.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InfoCategoryVO {
    private String key;
    private String label;
    private List<Map<String, Object>> items;
}
