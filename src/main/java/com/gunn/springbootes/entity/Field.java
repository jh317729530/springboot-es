package com.gunn.springbootes.entity;

import com.gunn.springbootes.elasticsearch.BaseDocument;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 场地父文档
 *
 * @author ganjunhui
 * @date 2020/1/12 3:10 下午
 */
@Data
public class Field extends BaseDocument {

    private Integer fieldId;

    private String fieldName;

    private String storeName;

    private Map<String, String> joinField;

    {
        joinField = new HashMap<>();
        joinField.put("name", "parent");
    }
}
