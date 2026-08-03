package com.gx.etl.scheme.vo;

import lombok.Data;

import java.util.List;

@Data
public class SchemeEquipVO {
    private Integer id;
    private String equipId;
    private String equipName;
    private String pid;
    private List<SchemeEquipVO> children;
}
