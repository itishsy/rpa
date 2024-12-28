package com.seebon.rpa.actions;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.SpringBeanHolder;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.actions.target.Target;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.utils.ELParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Component
public class ActionFactory {

    @Autowired
    private RobotContext ctx;

    public Action create(RobotFlowStep step) {
        Action action = SpringBeanHolder.getBean(step.getActionCode());
        if (action.getClass().isAnnotationPresent(RobotAction.class)) {
            /* 0） 初始化对象 */
            action.init(step);
            ctx.setAction(action);

            /* 1） 获取流程变量 */
            RobotAction robotAction = action.getClass().getAnnotation(RobotAction.class);
            Map<String, Object> ctxVars = new HashMap<>(ctx.getVariables());

            /* 2） 解析操作目标的配置参数 */
            Target target = (AbstractTarget) SpringBeanHolder.getBean(robotAction.targetType());
            if (StringUtils.isNotBlank(step.getTargetArgs())) {
                if (target instanceof TargetsTarget) {
                    String targetName = JSONObject.parseObject(step.getTargetArgs()).getString("targetName");
                    TargetsTarget targetsTarget = ((TargetsTarget) target);
                    targetsTarget.setTargetName(targetName);
                    target = targetsTarget.fetchTarget();
                    action.variable("targetName", targetName);
                }
                setArgs(target, step.getTargetArgs(), ctxVars);
            }

            /* 3） 提取目标对象，设置到action本地变量中 */
            fetchTarget(action, target);

            /* 4） 解析指令的行为参数*/
            if (StringUtils.isNotBlank(step.getActionArgs())) {
                ctxVars.putAll(action.getVariables());
                setArgs(action, step.getActionArgs(), ctxVars);
            }
            return action;
        }
        throw new RobotConfigException("未定义的指令。code=" + step.getActionCode());
    }

    public void fetchTarget(Action action, Target target) {
        Map<String, Object> objectMap = target.fetch();
        if (target.getClass().equals(ObjectTarget.class)) {
            String ctxKey = target.getClass().getSimpleName().replace(RobotConstant.TARGET_BEAN_SUFFIX, "");
            ctx.setVariable((ctxKey.substring(0, 1).toLowerCase() + ctxKey.substring(1)), target);
        }
        String targetKey = target.getClass().getSimpleName();
        action.variable(targetKey, target);
        for (String key : objectMap.keySet()) {
            Object obj = objectMap.get(key);
            if (action.getVariables().containsKey(key)) {
                Object ori = action.getVariables().get(key);
                if (obj != null && ori != null && !obj.equals(ori)) {
                    //一个action有多个target，转换为List<Object>
                    List<Object> list = (ori instanceof List) ? (List) ori : new ArrayList<>(Arrays.asList(ori));
                    list.add(obj);
                    action.variable(key, list);
                }
            } else {
                action.variable(key, obj);
            }
        }
    }

    private void setArgs(Object object, String args, Map<String, Object> variables) {
        JSONObject jsonObject = JSON.parseObject(args);
        Arrays.stream(object.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(ActionArgs.class))
                .forEach(field -> {
                    String key = field.getName();
                    Object value = jsonObject.get(key);
                    setParsedValue(object, field, value, variables);
                });
    }

    private void setParsedValue(Object object, Field field, Object value, Map<String, Object> variables) {
        ActionArgs actionArgs = field.getAnnotation(ActionArgs.class);
        try {
            Object parsedValue = actionArgs.parsed()
                    ? ELParser.parseObject(value, variables, field.getType())
                    : value;

            //没有解析，尝试类型转换
            if (null != parsedValue && !field.getType().isInstance(parsedValue)) {
                try {
                    parsedValue = Convert.convert(field.getType(), value);
                } catch (UnsupportedOperationException | ConvertException e) {
                    parsedValue = null;
                }
            }

            field.setAccessible(true);
            field.set(object, parsedValue);
            this.logArg(field, parsedValue);
        } catch (Exception e) {
            log.error("【Exception】", e);
            throw new RobotConfigException("参数配置错误。bean:" + object.getClass() + ",field:" + field.getName() + ",value:" + value);
        }
    }

    private void logArg(Field field, Object parsedValue) {
        if (parsedValue == null || StringUtils.isBlank(parsedValue.toString())) {
            return;
        }
        log.info("key=" + field.getName() + ",value=" + parsedValue);
    }
}
