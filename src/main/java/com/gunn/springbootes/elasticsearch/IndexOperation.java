package com.gunn.springbootes.elasticsearch;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ganjunhui
 * @date 2020/1/11 3:39 下午
 */
@Slf4j
@Component
public class IndexOperation {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @SneakyThrows
    public void createIndex(String indexName) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1));
        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

    @SneakyThrows
    public void deleteIndex(String indexName) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
    }

    public void createMapping(String indexName, String type, List<Property> properties) {
        PutMappingRequest request = new PutMappingRequest(indexName);
        request.type(type);
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            builder.startObject("properties");
            for (Property property : properties) {
                builder.startObject(property.getName());
                builder.field("type", property.getType());
                String analyzer = property.getAnalyzer();
                if (StringUtils.isNotBlank(analyzer)) {
                    builder.field("analyzer", analyzer);
                }
                Map<String, String> pluginSupportMap = property.getPluginSupportMap();
                if (null != pluginSupportMap && CollectionUtils.isNotEmpty(pluginSupportMap.entrySet())) {
                    for (Map.Entry<String, String> entry : pluginSupportMap.entrySet()) {
                        builder.field(entry.getKey(), entry.getValue());
                    }
                }

                Map<String, String[]> relations = property.getRelations();
                if (null != relations && CollectionUtils.isNotEmpty(relations.entrySet())) {
                    builder.startObject("relations");
                    for (Map.Entry<String, String[]> entry : relations.entrySet()) {
                        builder.field(entry.getKey(), entry.getValue());
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
            builder.endObject();
            request.source(builder);
        } catch (IOException e) {
            // TODO 打印日志
            log.error(e.getMessage(), e);
            return;
        }

        try {
            restHighLevelClient.indices().putMapping(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // TODO 打印日志
            log.error(e.getMessage(), e);
        }
    }
}
