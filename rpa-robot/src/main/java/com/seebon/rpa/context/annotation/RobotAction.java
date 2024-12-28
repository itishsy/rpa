package com.seebon.rpa.context.annotation;

import com.seebon.rpa.actions.target.impl.ObjectTarget;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 机器人指令
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Lazy(false)
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface RobotAction {

    /**
     * 操作名称
     *
     * @return
     */
    String name() default "";

    /**
     * 操作目标类型
     *
     * @return
     */
    Class<?> targetType() default ObjectTarget.class;

    /**
     * 显示顺序
     *
     * @return
     */
    int order() default 0;

    /**
     * 备注
     *
     * @return
     */
    String comment() default "";
}
