package com.gunn.springbootes.elasticsearch;

import com.google.common.collect.Maps;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author ganjunhui
 * @date 2020/1/11 10:14 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class IndexOperationTest {

    @Resource
    private IndexOperation indexOperation;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public String indexName = "test";

    public String typeName = "test_type";

    @Before
    public void setUp() {
//        indexOperation.deleteIndex(indexName);
        indexOperation.createIndex(indexName);
    }

    @After
    public void tearDown() {
        indexOperation.deleteIndex(indexName);
    }

    @Test
    public void testCreateMapping() throws IOException {
        List<Property> properties = new ArrayList<>();
        HashMap<String, String> pluginSupportMap = Maps.newHashMap();
        pluginSupportMap.put("search_analyzer", "ik_smart");

        properties.add(Property.builder().name("name").type("text").build());
        properties.add(Property.builder()
                .name("message")
                .type("text")
                .analyzer("ik_max_word")
                .pluginSupportMap(pluginSupportMap).build());
        indexOperation.createMapping(indexName, typeName, properties);

        GetMappingsRequest getMappingsRequest = new GetMappingsRequest();
        getMappingsRequest.indices(indexName);
        getMappingsRequest.types(typeName);
        GetMappingsResponse mapping = restHighLevelClient.indices().getMapping(getMappingsRequest, RequestOptions.DEFAULT);
        MappingMetaData mappingMetaData = mapping.getMappings().get(indexName).get(typeName);
        Map<String, Object> sourceAsMap = mappingMetaData.getSourceAsMap();
        System.out.println();
    }
}