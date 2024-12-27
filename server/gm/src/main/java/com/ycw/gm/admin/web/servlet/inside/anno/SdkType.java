package com.ycw.gm.admin.web.servlet.inside.anno;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface SdkType {
    int value();
}
