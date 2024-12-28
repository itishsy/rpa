package com.seebon.rpa.actions.impl.custom;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;

import java.util.Date;

@RobotAction(name = "执行Python语句", targetType = TargetsTarget.class, comment = "执行自定义Python语句")
public class ExePy extends AbstractAction {

    @ActionArgs("执行语句")
    private String expression;

    @ActionArgs("执行日期")
    private Date executionDate;

    @ActionArgs(value = "执行方法")
    private String functionName;

    @Override
    public void run() {

    }
}
