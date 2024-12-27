package com.ycw.core.internal.base.annotation;

import com.ycw.core.internal.base.config.AbstractConfig;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface JsonToBaseConfig {
    /**
     * json文件名
     */
    String fileName();

    /**
     * 文件主键
     */
    String key() default "id";

    /**
     * 解析需要的类
     */
    Class<?> clz() default AbstractConfig.class;
}