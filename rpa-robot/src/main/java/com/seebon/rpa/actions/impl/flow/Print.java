package com.seebon.rpa.actions.impl.flow;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.ELParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RobotAction(name = "打印日志", targetType = ObjectTarget.class, order = 100, comment = "打印信息")
public class Print extends AbstractAction {

    @ActionArgs(value = "信息", required = true)
    private String info;

    @Override
    public void run() {
        Object obj = ELParser.parse(info, ctx.getVariables(), Object.class);
        if (obj != null) {
            log.info("info：" + JSON.toJSONString(obj));
        }
    }
}
