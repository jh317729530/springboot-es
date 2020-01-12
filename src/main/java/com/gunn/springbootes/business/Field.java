package com.gunn.springbootes.business;

import com.google.common.collect.Maps;
import com.gunn.springbootes.elasticsearch.IndexOperation;
import com.gunn.springbootes.elasticsearch.Property;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gunn.springbootes.constant.ElasticSearchConst.Analyzer.IK_MAX_WORD;
import static com.gunn.springbootes.constant.ElasticSearchConst.Type.JOIN;
import static com.gunn.springbootes.constant.ElasticSearchConst.Type.TEXT;

/**
 * @author ganjunhui
 * @date 2020/1/12 2:21 下午
 */
@Component
public class Field {

    @Resource
    private IndexOperation indexOperation;

    public void createIndexAndMapping() {
        indexOperation.createIndex("field");

        List<Property> properties = new ArrayList<>();
        properties.add(Property.builder().name("fieldName").type(TEXT).analyzer(IK_MAX_WORD).pluginSupportMap(Property.IK).build());
        properties.add(Property.builder().name("storeName").type(TEXT).analyzer(IK_MAX_WORD).pluginSupportMap(Property.IK).build());

        HashMap<String, String[]> relations = Maps.newHashMap();
        relations.put("parent", new String[]{"date_child", "equipment_child"});

        properties.add(Property.builder().name("join_field").type(JOIN).relations(relations).build());
        indexOperation.createMapping("field", "field", properties);
    }
}
