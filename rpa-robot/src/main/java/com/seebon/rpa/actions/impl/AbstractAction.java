package com.seebon.rpa.actions.impl;

import cn.hutool.core.io.FileUtil;
import com.seebon.rpa.actions.Action;
import com.seebon.rpa.actions.target.Target;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.utils.ELParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractAction implements Action {

    @Autowired
    protected RobotContext ctx;

    private int timeout;
    protected long startTime;
    protected String skipTo;
    protected String falseSkipTo;
    protected String failedSkipTo;
    protected String stepCode;
    protected String stepName;
    @Setter
    protected String flowCode;
    private final Map<String, Object> variables = new HashMap<>();

    @Override
    public void init(RobotFlowStep step) {
        startTime = System.currentTimeMillis();
        if (step.getTimeout() == null || step.getTimeout() <= 0) {
            this.timeout = RobotConstant.DEFAULT_ACTION_TIMEOUT;
        } else {
            this.timeout = step.getTimeout();
        }
        if (RobotConstant.SUB_ACTION_CODE.equals(step.getActionCode())
                || RobotConstant.FINAL_ACTION_CODE.equals(step.getActionCode())) {
            this.timeout = 2 * 60 * 60;
        }
        stepCode = step.getStepCode();
        stepName = step.getStepName();
        flowCode = step.getFlowCode();
        skipTo = step.getSkipTo();
        falseSkipTo = step.getFalseSkipTo();
        failedSkipTo = step.getFailedSkipTo();
    }

    @Override
    public void variable(String key, Object object) {
        variables.put(key, object);
    }

    protected Object getVariable(String key) {
        if (variables.containsKey(key)) {
            return variables.get(key);
        }
        return null;
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public <T> T getTarget() {
        RobotAction robotAction = this.getClass().getAnnotation(RobotAction.class);
        Class<?> clz = robotAction.targetType();
        String key = null;
        if (TargetsTarget.class.equals(clz)) {
            key = variables.get("targetName").toString();
            key = key.substring(0, 1).toUpperCase() + key.substring(1);
        } else {
            key = clz.getSimpleName().replace(RobotConstant.TARGET_BEAN_SUFFIX, "");
            key = key.substring(0, 1).toLowerCase() + key.substring(1);
        }
        log.info("getTarget. key=" + key);
        Object object = variables.get(key);
        if (object == null) {
            object = ctx.getVariable(key);
        }
        if (null != object) {
            return (T) object;
        }
        return null;
    }

    public abstract void run();

    @Override
    public void release() {
        /*
         * 指令通用的释放操作包括：
         * 1. 清理action所有配置参数的值
         * 2. 清理action本地变量
         * 3. 执行target的release()方法
         * */
        Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(ActionArgs.class)).forEach(field -> {
            field.setAccessible(true);
            try {
                field.set(this, null);
            } catch (IllegalAccessException e) {
                log.error("【Exception】", e);
            }
        });
        for (String key : variables.keySet()) {
            Object o = variables.get(key);
            if (o instanceof Target) {
                ((Target) o).release();
            }
        }
        variables.clear();
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }

    protected <T> T parse(String content, Class<T> tClass) {
        Map<String, Object> vars = new HashMap<>(ctx.getVariables());
        vars.putAll(getVariables());
        return ELParser.parse(content, vars, tClass);
    }

    protected Object parseObject(String content) {
        Map<String, Object> vars = new HashMap<>(ctx.getVariables());
        vars.putAll(getVariables());
        return ELParser.parseObject(content, vars, null);
    }

    protected void cacheFilePath() {
        String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
        if (StringUtils.isBlank(downloadPath)) {
            return;
        }
        List<String> fileNames = FileUtil.listFileNames(downloadPath);
        if (CollectionUtils.isNotEmpty(fileNames)) {
            fileNames = fileNames.stream().filter(f -> !f.endsWith(".png")).collect(Collectors.toList());
        }
        ctx.setVariable(RobotConstant.CACHE_DOWNLOAD_FILE_NAME, fileNames);
    }
}
