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
@RobotAction(name = "win键盘", targetType = NoneTarget.class, comment = "win键盘操作")
public class WinKeyboard extends AbstractAction {

    @ActionArgs(value = "键入内容")
    private String keyIn;

    @ActionArgs(value = "输入参数")
    private String keyArgs;

    @Override
    public void run() {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Keyboard.py");
        commands.add(keyArgs);
        PythonUtil.runCommand("win键盘", "敲入成功", this.getTimeout(), commands);
    }
}
