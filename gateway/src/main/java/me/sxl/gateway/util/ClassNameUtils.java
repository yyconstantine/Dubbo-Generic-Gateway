package me.sxl.gateway.util;

/**
 * @author yyconstantine
 * @date 2020/5/27 上午 11:21
 */
public class ClassNameUtils {

    public static String simplifyClassName(String className) {
        return !className.contains("$") ? className : className.substring(0, className.indexOf("$"));
    }

}
