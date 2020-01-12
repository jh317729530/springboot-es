package com.gunn.springbootes.elasticsearch;

import com.google.common.base.CaseFormat;
import com.gunn.springbootes.annotation.Ignore;
import com.gunn.springbootes.annotation.Index;
import com.gunn.springbootes.annotation.Type;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private RestHighLevelClient restHighLevelClient;

    private Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private static final Map<Class, List<Field>> FIELDS = new ConcurrentHashMap<>();
    private static final Map<Class, String> INDEXNAMES = new ConcurrentHashMap<>();
    private static final Map<Class, String> TYPENAMES = new ConcurrentHashMap<>();


    /**
     * 在该类实例化后，缓存class的一些信息，减少多次使用反射带来的性能问题
     */
    @PostConstruct
    public void cacheFieldsAndIndexNamesAndTypeNames() {
        // 缓存每个字段的Field
        if (!FIELDS.containsKey(tClass)) {
            List<Field> fields = Arrays.stream(tClass.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(Ignore.class))
                    .collect(Collectors.toList());
            FIELDS.put(tClass, fields);
        }

        // 缓存索引名
        if (!INDEXNAMES.containsKey(tClass)) {
            if (tClass.isAnnotationPresent(Index.class)) {
                Index index = tClass.getAnnotation(Index.class);
                INDEXNAMES.put(tClass, index.value());
            } else {
                // 默认不声明的情况下，直接使用类名下划线作为index名
                INDEXNAMES.put(tClass, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tClass.getSimpleName()));
            }
        }

        // 缓存类型名
        if (!TYPENAMES.containsKey(tClass)) {
            if (tClass.isAnnotationPresent(Type.class)) {
                Type type = tClass.getAnnotation(Type.class);
                TYPENAMES.put(tClass, type.value());
            } else {
                TYPENAMES.put(tClass, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tClass.getSimpleName()));
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
        IndexRequest indexRequest = new IndexRequest(INDEXNAMES.get(tClass), TYPENAMES.get(tClass), ((BaseDocument) t).getId());
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
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            List<Field> fields = FIELDS.get(tClass);
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
