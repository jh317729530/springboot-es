package com.gunn.springbootes.entity;

import com.gunn.springbootes.annotation.Ignore;
import com.gunn.springbootes.annotation.Index;
import com.gunn.springbootes.annotation.Type;
import com.gunn.springbootes.elasticsearch.BaseDocument;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场地父文档
 *
 * @author ganjunhui
 * @date 2020/1/12 3:10 下午
 */
@Index("field")
@Type("field")
@Data
public class Field extends BaseDocument {

    private Integer fieldId;

    private String fieldName;

    private String storeName;

    @Ignore
    private Integer rentType;

    private Map<String, String> joinField;

    private List<FieldDate> fieldDates;

    {
        joinField = new HashMap<>();
        joinField.put("name", "parent");
    }

    @Data
    public static class FieldDate {
        private String relationDate;
        private Integer rentType;
    }
}
