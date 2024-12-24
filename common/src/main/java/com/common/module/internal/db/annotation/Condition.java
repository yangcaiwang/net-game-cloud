package com.common.module.internal.db.annotation;

import com.common.module.internal.db.constant.QueryCondition;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Condition {
    QueryCondition condition() default QueryCondition.eq;
}
