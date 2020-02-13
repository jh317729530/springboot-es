package com.gunn.springbootes.business;

import com.google.common.collect.Maps;
import com.gunn.springbootes.elasticsearch.DocumentOperation;
import com.gunn.springbootes.elasticsearch.IndexOperation;
import com.gunn.springbootes.elasticsearch.Property;
import com.gunn.springbootes.entity.Field;
import com.gunn.springbootes.util.JsonUtil;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
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


    /**
     * 创建索引和mapping
     */
    public void createIndexAndMapping() {
        indexOperation.createIndex("field");

        List<Property> properties = new ArrayList<>();
        properties.add(Property.builder().name("fieldId").type(INTEGER).build());
        properties.add(Property.builder().name("fieldName").type(TEXT).analyzer(IK_MAX_WORD).pluginSupportMap(Property.IK).build());
        properties.add(Property.builder().name("storeName").type(TEXT).analyzer(IK_MAX_WORD).pluginSupportMap(Property.IK).build());
        properties.add(Property.builder().name("fieldDates").type(NESTED).build());

        HashMap<String, String[]> relations = Maps.newHashMap();
        relations.put("parent", new String[]{"dateChild", "equipmentChild"});

        properties.add(Property.builder().name("joinField").type(JOIN).relations(relations).build());
        indexOperation.createMapping("field", "field", properties);
    }

    /**
     * 插入文档
     */
    public void index() {
        Field field = new Field();
        field.setId("1234_10");
        field.setFieldId(1234);
        field.setRentType(10);
        field.setFieldName("我的广州场地");
        field.setStoreName("我的广州空间");
        field.setRouting(null);

        List<Field.FieldDate> fieldDates = new ArrayList<>();
        Field.FieldDate fieldDate = new Field.FieldDate();
        fieldDate.setRelationDate("2020-01-13");
        fieldDate.setRentType(10);
        Field.FieldDate fieldDate1 = new Field.FieldDate();
        fieldDate1.setRelationDate("2020-01-14");
        fieldDate1.setRentType(10);
        fieldDates.add(fieldDate);
        fieldDates.add(fieldDate1);
        field.setFieldDates(fieldDates);
        indexDocument(field);
    }

    /**
     * 根据文档id查询
     * @param id
     * @return
     */
    public Field getById(String id) {
        Field byDocId = getByDocId(id);
        return byDocId;
    }

    /**
     * 通过场地开放日期查询到一个场地
     * @param relationDate
     * @return
     */
    public Field searchByFeildDates(String relationDate) {
        SearchRequest searchRequest = new SearchRequest("field");
        searchRequest.types("field");
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder query = SearchSourceBuilder.searchSource()
                .query(QueryBuilders.nestedQuery(
                        "fieldDates",
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.matchQuery("fieldDates.relationDate", relationDate)),
                        ScoreMode.None));


        searchRequest.source(query);

        try {
            System.out.println(searchRequest.source().toString());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            SearchHit hit = hits.getHits()[0];
            System.out.println(hit.getSourceAsString());
            return JsonUtil.getObjectFromJson(hit.getSourceAsString(), Field.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
