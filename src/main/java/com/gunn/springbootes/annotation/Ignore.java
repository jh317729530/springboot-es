package com.gunn.springbootes.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略字段，使得es插入、更新、查询时，无视该字段
 *
 * @author ganjunhui
 * @date 2020/1/12 3:29 下午
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
