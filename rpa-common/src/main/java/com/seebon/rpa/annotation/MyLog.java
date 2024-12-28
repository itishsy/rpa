package com.seebon.rpa.annotation;

/**
 * @author ZhenShijun
 * @dateTime 2021-04-07 17:19:35
 */


import java.lang.annotation.*;

/**
 * 自定义注解类
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface MyLog {

    /*调用方式：web：web请求访问，admin：管理后台，client：客户端接口调用, weChat: 微信小程序*/
    String callType() default "web";

    // 模块名称
    String moduleName() default "";

    // 页面名称
    String pageName() default "";

    // 操作名称
    String operation() default "";

    // 数据变更前的状态
    String prestatus() default "";

    // 数据变更后的状态
    String poststatus() default "";

    Class<?> beanClass() default Object.class;
}
