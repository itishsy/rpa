package com.seebon.rpa.http;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpScan {
    String[] value() default "";
}