package com.gunn.springbootes.elasticsearch;

import lombok.SneakyThrows;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ganjunhui
 * @date 2020/1/11 3:39 下午
 */
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
}
