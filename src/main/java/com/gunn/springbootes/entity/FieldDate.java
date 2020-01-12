package com.gunn.springbootes.entity;

import com.gunn.springbootes.annotation.Index;
import com.gunn.springbootes.annotation.Type;
import com.gunn.springbootes.elasticsearch.BaseDocument;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ganjunhui
 * @date 2020/1/12 3:19 下午
 */
@Index("field")
@Type("field")
@Data
public class FieldDate extends BaseDocument {

    private Integer fieldId;

    private Integer rentType;

    private String relationDate;

    private Map<String, String> joinField;

    {
        joinField = new HashMap<>();
        joinField.put("name", "dateChild");
    }

}
