package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.python.PythonUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

@RobotAction(name = "win属性", targetType = NoneTarget.class)
public class WinAttribute extends AbstractAction {

    @ActionArgs(value = "输入参数", required = true)
    private String inputArgs;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Attribute.py");
        commands.add(inputArgs);
        List<String> texts = PythonUtil.runCommand("win窗口属性", null, this.getTimeout(), commands);
        if (CollectionUtils.isNotEmpty(texts)) {
            String s = texts.get(0);
            s = s.replaceAll("'", "\"");
            Map<String, Object> obj = (Map<String, Object>) JsonUtils.jsonToBean(s, Map.class);
            ctx.setVariable(dataKey, obj);
        }
    }
}
