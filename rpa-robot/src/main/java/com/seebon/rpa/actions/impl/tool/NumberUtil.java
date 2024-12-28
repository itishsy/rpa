package com.seebon.rpa.actions.impl.tool;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.NumberMethod;
import com.seebon.rpa.utils.ELStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@ActionUtils
@RobotAction(name = "数值操作", targetType = ObjectTarget.class, comment = "数值操作")
public class NumberUtil extends AbstractAction {

    @ActionArgs(value = "操作类型", required = true, dict = NumberMethod.class)
    private String actionType;

    @ActionArgs(value = "操作参数")
    private String actionArgs;

    @ActionArgs(value = "结果变量")
    private String resultKey;

    @Override
    public void run() {
        switch (NumberMethod.valueOf(actionType)) {
            case sumList: {
                List list = getTarget();
                ctx.setVariable(resultKey, sumList(list, actionArgs));
                break;
            }
            case calculation: {
                ScriptEngine js = new ScriptEngineManager().getEngineByName("js");
                try {
                    String val = String.valueOf(js.eval(actionArgs));
                    ctx.setVariable(resultKey, new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
            default:
                break;
        }
    }

    public String sumList(List list, String key) {
        return ELStream.of(list).sum(key);
    }

    public String sumListToString(List<Map<String, Object>> list, String key) {
        return ELStream.of(list).sumToString(key);
    }

    public boolean numberEquals(String number1, String number2) {
        ScriptEngine js = new ScriptEngineManager().getEngineByName("js");
        try {
            Boolean eval = (Boolean) js.eval(number1.concat("==").concat(number2));
            return eval;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean numberDecide(String text) {
        ScriptEngine js = new ScriptEngineManager().getEngineByName("js");
        try {
            Boolean eval = (Boolean) js.eval(text);
            return eval;
        } catch (Exception e) {
            return false;
        }
    }

    public String numberAdd(String number1, String number2) {
        return new BigDecimal(number1).add(new BigDecimal(number2)).toString();
    }

    public String numberSub(String number1, String number2) {
        return new BigDecimal(number1).subtract(new BigDecimal(number2)).toString();
    }

    public String multiply(String number1, String number2) {
        return cn.hutool.core.util.NumberUtil.mul(number1, number2).toString();
    }

    public String div(String number1, String number2) {
        return cn.hutool.core.util.NumberUtil.div(number1, number2, 2).toString();
    }

    public String percentToNumber(String percentStr) {
        if (StringUtils.isBlank(percentStr)) {
            return "";
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        try {
            Number m = nf.parse(percentStr);
            return m.toString();
        } catch (Exception e) {
            log.error("字符转换异常." + e.getMessage(), e);
            return "";
        }
    }

    public boolean equals(String number1, String number2) {
        if (StringUtils.isBlank(number1) || StringUtils.isBlank(number2)) {
            return false;
        }
        return Integer.parseInt(number1) == Integer.parseInt(number2);
    }

    public boolean lt(String number1, String number2) {
        if (StringUtils.isBlank(number1) || StringUtils.isBlank(number2)) {
            return false;
        }
        return Integer.parseInt(number1) < Integer.parseInt(number2);
    }

    public boolean le(String number1, String number2) {
        if (StringUtils.isBlank(number1) || StringUtils.isBlank(number2)) {
            return false;
        }
        return Integer.parseInt(number1) <= Integer.parseInt(number2);
    }

    public boolean gt(String number1, String number2) {
        if (StringUtils.isBlank(number1) || StringUtils.isBlank(number2)) {
            return false;
        }
        return Integer.parseInt(number1) > Integer.parseInt(number2);
    }

    public boolean ge(String number1, String number2) {
        if (StringUtils.isBlank(number1) || StringUtils.isBlank(number2)) {
            return false;
        }
        return Integer.parseInt(number1) >= Integer.parseInt(number2);
    }
}
