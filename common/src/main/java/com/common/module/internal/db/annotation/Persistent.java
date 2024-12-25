
package com.common.module.internal.db.annotation;

import com.common.module.internal.db.constant.DataType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface Persistent {

    /**
     * 字段名，必须小写，用下划线做为多个单词的分割符,如果不写就是field的名称小写
     */
    String name() default "";

    /**
     * 数据类型,列出来的数据类型全部都设置了默认长度,只有varchar和浮点可以调整长度,只有浮点可以调整小数位
     */
    DataType dataType();

    /**
     * 是否允许为空
     */
    boolean canBeNull() default true;

    /**
     * 长度，0表示不限制,基本类型会自动设置,blob,text类型不允许设置长度,只有vachar和浮点类型才可以设置
     */
    int len() default 0;

    /**
     * 保留几位小数,只有float,double,decimal才可以设置,DECIMAL( M,D) ，如果M>D，为M+2否则为D+2
     */
    int scale() default 0;

    /**
     * 简单描述
     */
    String comment();
}
