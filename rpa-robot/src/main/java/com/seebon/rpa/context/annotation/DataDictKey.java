package com.seebon.rpa.context.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataDictKey {

    /**
     * 字典编码
     * @return
     */
    String value();

    /**
     * 字典名称
     * @return
     */
    String name();
}
