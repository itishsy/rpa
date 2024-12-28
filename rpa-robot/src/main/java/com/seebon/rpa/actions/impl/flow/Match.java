package com.seebon.rpa.actions.impl.flow;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import org.apache.commons.lang3.StringUtils;

@RobotAction(name = "条件判断",
        order = 0,
        targetType = TargetsTarget.class,
        comment = "解析匹配条件，返回结果为true执行Skip")
public class Match extends AbstractAction {

    @ActionArgs(value = "匹配条件", style = DynamicFieldType.text)
    private Boolean condition;

    @Override
    public void run() {
        if (condition) {
            throw new RuntimeSkipTo(skipTo);
        } else {
            if (StringUtils.isNotBlank(failedSkipTo)) {
                throw new RuntimeSkipTo(failedSkipTo);
            }
            if (StringUtils.isNotBlank(falseSkipTo)) {
                throw new RuntimeSkipTo(falseSkipTo);
            }
            throw new RuntimeSkipTo(RobotConstant.NEXT_STEP);
        }
    }
}
