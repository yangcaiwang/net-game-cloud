package com.ycw.core.network.netty.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebSocketCmd {
    int reqCmd();
    int respCmd();
    String comment();
}
