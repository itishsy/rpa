package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RobotAction(name = "win输入", targetType = NoneTarget.class)
public class WinInput extends AbstractAction {

    @ActionArgs(value = "输入参数")
    private String inputArgs;

    @ActionArgs("输入内容")
    private String inputValue;

    @Override
    public void run() {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Input.py");
        commands.add(inputArgs);
        commands.add(inputValue);
        PythonUtil.runCommand("win输入", "输入成功", this.getTimeout(), commands);
    }
}
