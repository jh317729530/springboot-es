package com.gunn.springbootes.business;

import com.google.common.collect.Maps;
import com.gunn.springbootes.elasticsearch.DocumentOperation;
import com.gunn.springbootes.elasticsearch.IndexOperation;
import com.gunn.springbootes.elasticsearch.Property;
import com.gunn.springbootes.entity.Field;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gunn.springbootes.constant.ElasticSearchConst.Analyzer.IK_MAX_WORD;
import static com.gunn.springbootes.constant.ElasticSearchConst.Type.*;

/**
 * @author ganjunhui
 * @date 2020/1/12 2:21 下午
 */
@Component
public class FieldQuery extends DocumentOperation<Field> {

    @Resource
    private IndexOperation indexOperation;


    public void createIndexAndMapping() {
        indexOperation.createIndex("field");

        List<Property> properties = new ArrayList<>();
        properties.add(Property.builder().name("fieldId").type(INTEGER).build());
        properties.add(Property.builder().name("fieldName").type(TEXT).analyzer(IK_MAX_WORD).pluginSupportMap(Property.IK).build());
        properties.add(Property.builder().name("storeName").type(TEXT).analyzer(IK_MAX_WORD).pluginSupportMap(Property.IK).build());

        HashMap<String, String[]> relations = Maps.newHashMap();
        relations.put("parent", new String[]{"dateChild", "equipmentChild"});

        properties.add(Property.builder().name("joinField").type(JOIN).relations(relations).build());
        indexOperation.createMapping("field", "field", properties);
    }

    public void index() {
        Field field = new Field();
        field.setId("1234_10");
        field.setFieldId(1234);
        field.setFieldName("我的广州场地");
        field.setStoreName("我的广州空间");
        indexDocument(field);
    }

    public Field getById(String id) {
        Field byDocId = getByDocId(id);
        return byDocId;
    }
}
