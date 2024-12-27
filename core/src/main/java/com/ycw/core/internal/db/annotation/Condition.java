package com.ycw.core.internal.db.annotation;

import com.ycw.core.internal.db.constant.QueryCondition;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Condition {
    QueryCondition condition() default QueryCondition.eq;
}
