
package com.common.module.internal.script;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ScriptParams {

	/**
	 * 表明参数类型,按顺序
	 */
	Class<?>[] paramTypes();
}
