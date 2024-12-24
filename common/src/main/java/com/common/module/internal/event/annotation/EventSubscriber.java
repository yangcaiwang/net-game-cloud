
package com.common.module.internal.event.annotation;

import java.lang.annotation.*;

/**
 * 事件消费者
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EventSubscriber {

}
