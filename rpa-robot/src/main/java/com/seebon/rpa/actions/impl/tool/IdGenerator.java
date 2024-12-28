package com.seebon.rpa.actions.impl.tool;

import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.IDType;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@ActionUtils
@RobotAction(name = "ID生成器",
        targetType = NoneTarget.class,
        comment = "唯一码生成操作")
public class IdGenerator extends AbstractAction {

    @ActionArgs(value = "ID类型", required = true, dict = IDType.class)
    private String idType;

    @ActionArgs(value = "参数")
    private String actionArgs;

    @ActionArgs(value = "结果变量", required = true)
    private String resultKey;

    @Override
    public void run() {
        Object result = null;
        switch (IDType.valueOf(idType)) {
            case seconds:
                result = seconds();
                break;
            case timestamp:
                result = timestamp();
                break;
            case uuid:
                result = uuid();
                break;
            default:
                break;
        }
        ctx.setVariable(resultKey, result);
    }

    public String seconds() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(new Date());
    }

    public long timestamp() {
        return System.currentTimeMillis();
    }

    public String uuid() {
        return UUIDGenerator.uuidStringWithoutLine();
    }

}
