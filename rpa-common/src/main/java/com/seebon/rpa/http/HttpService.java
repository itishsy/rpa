package com.seebon.rpa.http;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
public @interface HttpService {
    String value() default "";
}