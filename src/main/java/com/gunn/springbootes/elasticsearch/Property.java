package com.gunn.springbootes.elasticsearch;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建mapping时，对应properties下每个字段的说明
 *
 * @author ganjunhui
 * @date 2020/1/11 9:09 下午
 */
@Data
@Builder
public class Property {

    public static final Map<String, String> IK = new HashMap<>();

    static {
        IK.put("search_analyzer", "ik_smart");
    }

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    private String analyzer;

    private Map<String, String[]> relations;

    /**
     * 存储插件支持的key-value
     * 例如 使用到ik插件，则需要传入对应的key-value
     */
    private Map<String, String> pluginSupportMap;
}
