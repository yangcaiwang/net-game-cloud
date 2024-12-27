
package com.ycw.core.internal.db.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface PK {

    /**
     * 是否自增，默认不自增
     */
    boolean auto() default false;

    /**
     * 起始值，默认是1
     */
    long begin() default 1;

    /**
     * 是否需要校验主键长度，默认校验需要long类型主键需要18位，否则不需校验
     **/
    boolean needCheckLength() default true;
}
