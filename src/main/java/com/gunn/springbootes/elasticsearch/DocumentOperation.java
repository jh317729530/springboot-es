package com.gunn.springbootes.elasticsearch;

import com.gunn.springbootes.annotation.Ignore;
import com.gunn.springbootes.annotation.Index;
import com.gunn.springbootes.annotation.Type;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * 封装文档操作
 *
 * @author ganjunhui
 * @date 2020/1/12 2:54 下午
 */
@Slf4j
public abstract class DocumentOperation<T> {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 插入文档
     */
    protected void indexDocument(T t) {
        if (!(t instanceof BaseDocument)) {
            throw new IllegalArgumentException("index object is not instance of BaseDocument");
        }
        Class<?> clazz = t.getClass();
        if (!clazz.isAnnotationPresent(Index.class) || !clazz.isAnnotationPresent(Type.class)) {
            throw new IllegalArgumentException("index class has no @Index or @Type");
        }
        Index index = clazz.getAnnotation(Index.class);
        Type type = clazz.getAnnotation(Type.class);
        String indexName = index.value();
        String typeName = type.value();
        IndexRequest indexRequest = new IndexRequest(indexName, typeName, ((BaseDocument)t).getId());
        indexRequest.routing(((BaseDocument) t).getRouting());
        XContentBuilder builder = null;
        try {
            builder = entityToBuilder(t);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
        indexRequest.source(builder);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

//    protected T getByDocId(String fieldId) {
//
//    }

    /**
     * 将实体类型转换为对应的builder
     *
     * @param t
     * @return
     */
    private XContentBuilder entityToBuilder(T t) throws Exception {
        Class<?> entityClazz = t.getClass();
        Field[] fields = entityClazz.getDeclaredFields();

        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Ignore.class)) {
                    setBuilderField(builder, field, t);
                }
            }
            builder.endObject();
            return builder;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new Exception("can not create XContentBuilder by IOException");
        }
    }

    private void setBuilderField(XContentBuilder builder, Field field, T t) {
        try {
            // 字段对应的类型
            Class<?> fieldClazz = field.getType();
            String name = field.getName();
            Object o = field.get(t);
            if (Date.class.equals(fieldClazz)) {
                builder.timeField(name, o);
            } else {
                builder.field(name, o);
            }
        } catch (IllegalAccessException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
