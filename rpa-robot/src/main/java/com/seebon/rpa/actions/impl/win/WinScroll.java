package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.python.PythonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RobotAction(name = "win滚动条", targetType = NoneTarget.class)
public class WinScroll extends AbstractAction {

    @ActionArgs(value = "轮向距离", required = true, comment = "大于0向上滚动，小于0向下滚动")
    private String wheelDist;

    @ActionArgs(value = "横坐标", comment = "滑动区域的任意横坐标")
    private String xPoint;

    @ActionArgs(value = "纵坐标", comment = "滑动区域的任意纵坐标")
    private String yPoint;

    @Override
    public void run() {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Mouse.py");
        commands.add("scroll");
        commands.add(StringUtils.isBlank(xPoint) ? "0" : xPoint);
        commands.add(StringUtils.isBlank(yPoint) ? "0" : yPoint);
        commands.add(wheelDist);
        PythonUtil.runCommand("win滚动条", "滚动成功", this.getTimeout(), commands);
    }
}
