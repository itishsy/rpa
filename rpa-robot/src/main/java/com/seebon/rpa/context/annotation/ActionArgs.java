package com.seebon.rpa.context.annotation;

import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.constant.ArgsScope;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionArgs {

    /**
     * 参数名称
     *
     * @return
     */
    String value() default "";

    /**
     * 是否Action创建时解析。参数需要在run()解析，设置为false，
     *
     * @return
     */
    boolean parsed() default true;

    /**
     * 是否必填校验
     *
     * @return
     */
    boolean required() default false;

    /**
     * 变量作用域
     *
     * @return
     */
    ArgsScope scope() default ArgsScope.LOC;

    /**
     * 候选项
     *
     * @return
     */
    Class<?> dict() default Object.class;

    /**
     * 字段显示风格
     *
     * @return
     */
    DynamicFieldType style() default DynamicFieldType.undefined;

    /**
     * 参数说明
     *
     * @return
     */
    String comment() default "";
}
