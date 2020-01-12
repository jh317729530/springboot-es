package com.gunn.springbootes;

import com.gunn.springbootes.elasticsearch.IndexOperation;
import com.gunn.springbootes.elasticsearch.Property;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

	private static IndexOperation indexOperation;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		indexOperation.deleteIndex("test");
		indexOperation.createIndex("test");
		List<Property> properties = new ArrayList<>();
		properties.add(Property.builder().name("name").type("text").build());
		indexOperation.createMapping("test", "testType", properties);
	}

	@Resource
	public void setIndexOperation(IndexOperation indexOperation) {
		Application.indexOperation = indexOperation;
	}


}
