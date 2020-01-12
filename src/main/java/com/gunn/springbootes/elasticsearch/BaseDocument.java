package com.gunn.springbootes.elasticsearch;

import lombok.Data;

/**
 * @author ganjunhui
 * @date 2020/1/12 4:12 下午
 */
@Data
public class BaseDocument {

    protected String index;

    protected String type;

    protected String id;

    protected String version;
}
