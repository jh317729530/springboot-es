package com.gunn.springbootes;

import com.gunn.springbootes.business.FieldQuery;
import com.gunn.springbootes.elasticsearch.IndexOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class Application {

	private static IndexOperation indexOperation;

	private static FieldQuery fieldQuery;


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
//		indexOperation.deleteIndex("test");
//		indexOperation.createIndex("test");
//		List<Property> properties = new ArrayList<>();
//		properties.add(Property.builder().name("name").type("text").build());
//		indexOperation.createMapping("test", "testType", properties);

		indexOperation.deleteIndex("field");
		fieldQuery.createIndexAndMapping();
		fieldQuery.index();
	}

	@Resource
	public void setIndexOperation(IndexOperation indexOperation) {
		Application.indexOperation = indexOperation;
	}

	@Resource
	public void setField(FieldQuery fieldQuery) {
		Application.fieldQuery = fieldQuery;
	}
}
