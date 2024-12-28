package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.AppClickType;
import com.seebon.rpa.context.enums.WindowActionType;
import com.seebon.rpa.utils.python.PythonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RobotAction(name = "win窗口", targetType = NoneTarget.class, comment = "win窗口操作")
public class WinWindow extends AbstractAction {


    @ActionArgs(value = "操作方式", required = true, dict = WindowActionType.class)
    private String actionType;

    @ActionArgs(value = "窗口参数",comment = "元素定位")
    private String windowArgs;


    @Override
    public void run() {
        switch (WindowActionType.valueOf(actionType)) {
            case maximizing:
            case minimize:
            case active: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\Window.py");
                commands.add(windowArgs);
                commands.add(actionType);
                PythonUtil.runCommand("win窗口操作", "操作成功", this.getTimeout(), commands);
                break;
            }
            default:
                break;
        }
    }
}
