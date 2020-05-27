package me.sxl.gateway.annotation;

import me.sxl.gateway.model.constant.Constants;

import java.lang.annotation.*;

/**
 * @author yyconstantine
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboApi {

    /**
     * 接口超时时间
     */
    int timeout() default 5000;

    /**
     * 接口请求类型
     */
    String method() default "POST";

    /**
     * 是否允许泛化调用
     */
    boolean generic() default true;

    /**
     * 接口owner
     */
    String owner() default "";

    /**
     * 接口是否可用
     */
    boolean available() default true;

    /**
     * 接口名称
     */
    String api() default "";

    /**
     * 接口重试次数,0为默认重试3次,-1为不重试,请考虑接口幂等后设置
     */
    int retries() default 0;

    /**
     * 接口版本
     */
    String version() default Constants.V1_VERSION;

}
