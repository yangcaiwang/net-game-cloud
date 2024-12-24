
package com.common.module.internal.db.annotation;

import java.lang.annotation.*;

/**
 * MySQL主键字段，可以标记多个表示联合主键</br>
 * PS:如果联合主键中有自增主键,需注意</br>
 * 1：存储引擎必须为MyISAM</br>
 * 2：自增主键必须为第二列</br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface PK {

	/** 是否自增，默认不自增 */
	boolean auto() default false;

	/** 起始值，默认是1 */
	long begin() default 1;

	/** 是否需要校验主键长度，默认校验需要long类型主键需要18位，否则不需校验 **/
	boolean needCheckLength() default true;
}
