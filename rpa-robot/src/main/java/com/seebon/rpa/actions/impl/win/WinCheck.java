package com.seebon.rpa.actions.impl.win;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.AppAction;
import com.seebon.rpa.context.enums.CheckType;
import com.seebon.rpa.utils.python.PythonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RobotAction(name = "win检查", targetType = NoneTarget.class)
public class WinCheck extends AbstractAction {

    @Conditions({"app:checkArgs,actionType,checkName,dataKey", "element:checkArgs,dataKey"})
    @ActionArgs(value = "检查方式", required = true, dict = CheckType.class)
    private String checkType;

    @ActionArgs(value = "检查参数", required = true)
    private String checkArgs;

    @ActionArgs(value = "检查名称")
    private String checkName;

    @ActionArgs(value = "应用操作", required = true, dict = AppAction.class)
    private String actionType;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        switch (CheckType.valueOf(checkType)) {
            case app: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\CheckApp.py");
                commands.add(StringUtils.defaultIfBlank(checkName, this.getExeName(Lists.newArrayList(checkArgs))));

                List<String> lineList = PythonUtil.runCommand("win应用检查", null, this.getTimeout(), commands);
                if (this.isTrue(lineList)) {
                    ctx.setVariable(dataKey, "true");
                    if (!ctx.getVariables().containsKey("appPath")) {
                        ctx.setVariable("appPath", checkArgs);
                    }
                    if (!ctx.getVariables().containsKey("appType")) {
                        ctx.setVariable("appType", actionType);
                    }
                } else {
                    ctx.setVariable(dataKey, "false");
                }
                break;
            }
            case element: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\CheckElement.py");
                commands.add(checkArgs);

                List<String> lineList = PythonUtil.runCommand("win元素检查", null, this.getTimeout(), commands);
                if (this.isTrue(lineList)) {
                    ctx.setVariable(dataKey, "true");
                } else {
                    ctx.setVariable(dataKey, "false");
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean isTrue(List<String> listener) {
        if (CollectionUtils.isEmpty(listener)) {
            return false;
        }
        List<String> list = listener.stream().map(it -> it.toLowerCase()).collect(Collectors.toList());
        if (list.contains("true")) {
            return true;
        }
        return false;
    }

    private String getExeName(List<String> paths) {
        return paths.stream().map(path -> StringUtils.substringAfterLast(path, "\\")).collect(Collectors.joining(","));
    }
}
