package com.seebon.rpa.actions.impl.data;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.MethodName;

import java.util.Map;

@RobotAction(name = "实体操作", order = 1)
public class MapOperate extends AbstractAction {

    @Conditions({
            "putAll:mapInfo,dataKey",
            "contains:fieldName,dataKey",
            "clear:dataKey",
    })
    @ActionArgs(value = "方法名称", required = true, dict = MethodName.class)
    private String methodName;

    @ActionArgs(value = "字段名", required = true)
    private String fieldName;

    @ActionArgs(value = "修改信息", required = true, comment = "object")
    private Map<String, Object> mapInfo;

    @ActionArgs(value = "结果变量")
    private String dataKey;

    @Override
    public void run() {
        Map<String, Object> mapOperand = this.getTarget();
        switch (MethodName.valueOf(methodName)) {
            case putAll: {
                if (mapInfo != null && !mapInfo.isEmpty()) {
                    mapOperand.putAll(mapInfo);
                }
                break;
            }
            case clear: {
                mapOperand.clear();
                break;
            }
            case contains: {
                ctx.setVariable(dataKey, mapOperand.containsKey(fieldName));
                break;
            }
            default:
                break;
        }
    }
}
