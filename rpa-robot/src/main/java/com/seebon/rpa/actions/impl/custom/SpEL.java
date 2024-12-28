package com.seebon.rpa.actions.impl.custom;

import com.seebon.rpa.SpringBeanHolder;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Set;

@RobotAction(name = "执行EL表达式", targetType = TargetsTarget.class, comment = "自定义执行EL表达式，")
public class SpEL extends AbstractAction {

    private Boolean result = null;

    @ActionArgs("执行语句")
    private String expression;

    @ActionArgs("结果类型")
    private String resultType;

    @ActionArgs("存放变量名")
    private String resultKey;

    /**
     * SpEL语法：
     * 变量引用: #variableName, 如： #name
     * 实例方法调用: @beanName, 如： @browser.run()
     * 静态方法调用: T(ClassName), 如： T(cn.hutool.core.date.DateUtil).now()
     */
    @Override
    public void run() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        BeanFactoryResolver factoryResolver = new BeanFactoryResolver(SpringBeanHolder.applicationContext);
        Set<String> set = ctx.getVariables().keySet();
        for (String key : set) {
            context.setVariable(key, ctx.getVariable(key));
        }
        context.setBeanResolver(factoryResolver);

        ExpressionParser parser = new SpelExpressionParser();
        Expression parseExpression = parser.parseExpression(expression);

        Object object = parseExpression.getValue(context);
        if (StringUtils.isNotBlank(resultKey)) {
            ctx.setVariable(resultKey, object);
        }
        if ("boolean".equalsIgnoreCase(resultType) || "bool".equalsIgnoreCase(resultType)) {
            result = (Boolean) object;
        }
    }
}
