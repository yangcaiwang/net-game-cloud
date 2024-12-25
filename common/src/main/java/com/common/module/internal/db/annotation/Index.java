
package com.common.module.internal.db.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface Index {

    /**
     * 普通索引
     */
    String INDEX_TYPE_NORMAL = "NORMAL";

    /**
     * 全文索引
     */
    String INDEX_TYPE_FULLTEXT = "FULLTEXT";

    /**
     * 唯一索引
     */
    String INDEX_TYPE_UNIQUE = "UNIQUE";

    /**
     * 索引方式-hash
     */
    String INDEX_WAY_HASH = "HASH";

    /**
     * 索引方式-二叉树
     */
    String INDEX_WAY_BTREE = "BTREE";

    /**
     * 索引名称-必须小写,（如果没填，默认使用字段的持久化名称，但如果是联合索引就不行）
     */
    String name() default "";

    /**
     * 索引类型 UNIQUE or NORMAL or FULLTEXT
     */
    String type() default INDEX_TYPE_NORMAL;

    /**
     * 索引方式 HASH or BTREE
     */
    String way() default INDEX_WAY_BTREE;

}
