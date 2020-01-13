package com.gunn.springbootes;

import com.gunn.springbootes.business.FieldDateQuery;
import com.gunn.springbootes.business.FieldQuery;
import com.gunn.springbootes.elasticsearch.IndexOperation;
import com.gunn.springbootes.entity.Field;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class Application {

	private static IndexOperation indexOperation;

	private static FieldQuery fieldQuery;

	private static FieldDateQuery fieldDateQuery;


	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class, args);
//		indexOperation.deleteIndex("test");
//		indexOperation.createIndex("test");
//		List<Property> properties = new ArrayList<>();
//		properties.add(Property.builder().name("name").type("text").build());
//		indexOperation.createMapping("test", "testType", properties);

		indexOperation.deleteIndex("field");
		fieldQuery.createIndexAndMapping();
		fieldQuery.index();
		fieldDateQuery.index();

		Field byId = fieldQuery.getById("1234_10");
		// 执行查询时，文档并未完全插入，所以线程休眠一段时间
		Thread.sleep(2000);
		Field field = fieldQuery.searchByFeildDates("2020-01-13");
		System.out.println("end");
	}

	@Resource
	public void setIndexOperation(IndexOperation indexOperation) {
		Application.indexOperation = indexOperation;
	}

	@Resource
	public void setField(FieldQuery fieldQuery) {
		Application.fieldQuery = fieldQuery;
	}

	@Resource
	public void setFieldDateQuery(FieldDateQuery fieldDateQuery) {
		Application.fieldDateQuery = fieldDateQuery;
	}
}
