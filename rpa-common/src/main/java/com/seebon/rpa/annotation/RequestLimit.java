package com.seebon.rpa.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-26 19:10:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {

    /*规定时间内的允许最大请求次数*/
    int count() default Integer.MAX_VALUE;

    /*限定的时间，单位：毫秒，默认是1分钟*/
    long time() default 60000;

}
