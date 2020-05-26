package me.sxl.gateway.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboApi {

    int timeout() default 5000;

    boolean generic() default true;

}
