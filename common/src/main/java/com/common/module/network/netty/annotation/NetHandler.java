package com.common.module.network.netty.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NetHandler {
    int reqCmd();
    int respCmd();
    String comment();
    // 活动id，大于0就会校验功能开启，如果没有开启，则返回错误
    int actId() default -1;
    // 功能id，大于0就会校验功能开启，如果没有开启，则返回错误
    int funcId() default -1;
}
