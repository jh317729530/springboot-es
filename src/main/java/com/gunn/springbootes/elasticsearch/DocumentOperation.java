package com.gunn.springbootes.elasticsearch;

import com.google.common.base.CaseFormat;
import com.gunn.springbootes.annotation.Ignore;
import com.gunn.springbootes.annotation.Index;
import com.gunn.springbootes.annotation.Type;
import com.gunn.springbootes.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封装文档操作
 *
 * @author ganjunhui
 * @date 2020/1/12 2:54 下午
 */
@Slf4j
public abstract class DocumentOperation<T> {

    @Resource
    protected RestHighLevelClient restHighLevelClient;

    private Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private List<Field> fields;
    private String indexName;
    private String typeName;

    /**
     * 在该类实例化后，缓存class的一些信息，减少多次使用反射带来的性能问题
     */
    @PostConstruct
    public void cacheFieldsAndIndexNamesAndTypeNames() {
        // 缓存每个字段的Field
        if (CollectionUtils.isEmpty(fields)) {
            fields = Arrays.stream(tClass.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(Ignore.class))
                    .collect(Collectors.toList());
        }

        // 缓存索引名
        if (StringUtils.isBlank(indexName)) {
            if (tClass.isAnnotationPresent(Index.class)) {
                Index index = tClass.getAnnotation(Index.class);
                indexName = index.value();
            } else {
                // 默认不声明的情况下，直接使用类名下划线作为index名
                indexName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tClass.getSimpleName());
            }
        }

        // 缓存类型名
        if (StringUtils.isBlank(typeName)) {
            if (tClass.isAnnotationPresent(Type.class)) {
                Type type = tClass.getAnnotation(Type.class);
                typeName = type.value();
            } else {
                typeName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tClass.getSimpleName());
            }
        }
    }


    /**
     * 插入文档
     */
    protected void indexDocument(T t) {
        if (!(t instanceof BaseDocument)) {
            throw new IllegalArgumentException("index object is not instance of BaseDocument");
        }
        IndexRequest indexRequest = new IndexRequest(indexName, typeName, ((BaseDocument) t).getId());
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
            restHighLevelClient.index(indexRequest);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据文档id获取文档
     *
     * @param id
     * @return
     */
    protected T getByDocId(String id) {
        GetRequest getRequest = new GetRequest(indexName, typeName, id);
        GetResponse response = null;
        try {
            response = restHighLevelClient.get(getRequest);
            T t = JsonUtil.getObjectFromJson(response.getSourceAsString(), tClass);
            ((BaseDocument) t).setId(id);
            ((BaseDocument) t).setIndex(indexName);
            ((BaseDocument) t).setVersion(response.getVersion());
            ((BaseDocument) t).setType(typeName);
            return t;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将实体类型转换为对应的builder
     *
     * @param t
     * @return
     */
    private XContentBuilder entityToBuilder(T t) throws Exception {
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
            } else if (List.class.equals(fieldClazz)) {
                builder.field(name, ((List) o).stream().map(JsonUtil::objectToMap).collect(Collectors.toList()));
            } else {
                builder.field(name, o);
            }
        } catch (IllegalAccessException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
