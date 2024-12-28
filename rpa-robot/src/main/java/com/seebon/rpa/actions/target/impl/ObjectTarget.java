package com.seebon.rpa.actions.target.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.SpringBeanHolder;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.utils.ELParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ActionTarget
public class ObjectTarget extends AbstractTarget<Object> {

    @Setter
    @ActionArgs(value = "操作对象", parsed = false, comment = "变量名、Json、表达式")
    private String text;

    @Autowired
    private TargetsTarget targetsTarget;

    @Override
    public Object fetchTarget() {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Object object = null;
        if (text.startsWith("{") && text.endsWith("}")) {
            JSONObject jsonObject = JSONObject.parseObject(text);
            boolean targetsFlag = true;

            Map<String, Object> targets = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                if (allTargetName().contains(key + RobotConstant.TARGET_BEAN_SUFFIX)) {
                    targets.put(key, jsonObject.get(key));
                } else {
                    targetsFlag = false;
                    break;
                }
            }
            if (targetsFlag) {
                object = targetsTarget.fetchTarget();
            } else {
                object = jsonObject;
            }
        } else if (text.startsWith("[") && text.endsWith("]")) {
            object = JSONArray.parseArray(text);
        } else if (ELParser.isExpression(text)) {
            object = ELParser.parseObject(text, ctx.getVariables(), null);
        } else if (ctx.contains(text)) {
            object = ctx.getVariable(text);
        } else {
            object = text;
        }
        return object;
    }

    private List<String> allTargetName() {
        return Arrays.asList(SpringBeanHolder.applicationContext.getBeanNamesForAnnotation(ActionTarget.class));
    }
}
