package com.gunn.springbootes.business;

import com.gunn.springbootes.elasticsearch.DocumentOperation;
import com.gunn.springbootes.entity.FieldDate;
import org.springframework.stereotype.Component;

/**
 * @author ganjunhui
 * @date 2020/1/12 4:32 下午
 */
@Component
public class FieldDateQuery extends DocumentOperation<FieldDate> {

    public void index() {
        FieldDate fieldDate = new FieldDate();
        fieldDate.setFieldId(1234);
        fieldDate.setRentType(10);
        fieldDate.setRelationDate("2020-01-12");
        fieldDate.getJoinField().put("parent", "1234_10");
        fieldDate.setRouting("1234_10");
        indexDocument("field", "field", fieldDate);
    }
}
