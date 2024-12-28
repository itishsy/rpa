package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.AppClickType;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@RobotAction(name = "win点击", targetType = NoneTarget.class)
public class WinClick extends AbstractAction {

    @ActionArgs(value = "点击方式", required = true, dict = AppClickType.class)
    @Conditions({"click:clickArgs,xPoint,yPoint", "doubleClick:xPoint,yPoint", "mouseClick:xPoint,yPoint"})
    private String clickType;

    @ActionArgs(value = "点击参数")
    private String clickArgs;

    @ActionArgs(value = "横坐标", comment = "非鼠标点击时，视为点击位置的横坐标的偏移量")
    private String xPoint;

    @ActionArgs(value = "纵坐标", comment = "非鼠标点击时，视为点击位置的纵坐标的偏移量")
    private String yPoint;

    @Override
    public void run() {
        switch (AppClickType.valueOf(clickType)) {
            case click: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\Click.py");
                commands.add(clickArgs);
                commands.add(StringUtils.isBlank(xPoint) ? "0" : xPoint);
                commands.add(StringUtils.isBlank(yPoint) ? "0" : yPoint);
                PythonUtil.runCommand("win点击", "点击成功", this.getTimeout(), commands);
                break;
            }
            case mouseClick: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\Mouse.py");
                commands.add("leftClick");
                commands.add(xPoint);
                commands.add(yPoint);
                PythonUtil.runCommand("win鼠标点击", "点击成功", this.getTimeout(), commands);
                break;
            }
            default:
                break;
        }
    }
}
