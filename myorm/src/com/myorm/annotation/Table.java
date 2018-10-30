package com.myorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 该注解作用在持久化类上面，如果没有使用@Table注解，持久化类对应的数据库名和类名一致
* */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    //定义一个name方法，默认值为空字符串
    String name() default "";
}
