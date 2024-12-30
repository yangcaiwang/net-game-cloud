
package com.ycw.core.network.jetty.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpCmd {
    int cmd();
    String comment();
}
