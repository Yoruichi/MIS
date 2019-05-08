package me.yoruichi.mis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Yoruichi
 * @Date: 2019/5/8 3:15 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    String name() default  "getCode";
    String decode() default  "codeOf";
}
