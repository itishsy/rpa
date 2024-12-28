package com.seebon.rpa.context.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conditions {

    /**
     * 取值
     * @return
     */
    String[] value() default "";
}
