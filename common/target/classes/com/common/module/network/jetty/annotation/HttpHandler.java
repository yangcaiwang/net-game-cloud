
package com.common.module.network.jetty.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HttpHandler {

	/**
	 * 指令
	 */
	int cmd();

	/**
	 * 描述
	 */
	String comment();
}
