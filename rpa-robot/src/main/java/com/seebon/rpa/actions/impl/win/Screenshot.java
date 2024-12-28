package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.python.PythonUtil;

import java.util.List;

@RobotAction(name = "win截图", targetType = NoneTarget.class)
public class Screenshot extends AbstractAction {

    @ActionArgs(value = "输入参数")
    private String inputArgs;

    @ActionArgs(value = "文件路径", required = true)
    private String filePath;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Screenshot.py");
        commands.add(inputArgs);
        commands.add(filePath);
        ctx.addVariable(dataKey, filePath);
        PythonUtil.runCommand("win截图", "截图成功", this.getTimeout(), commands);
    }
}
