package com.seebon.rpa.context.annotation;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Lazy(false)
@Service
public @interface ActionUtils {

}
