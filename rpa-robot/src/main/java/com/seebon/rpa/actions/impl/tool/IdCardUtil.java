package com.seebon.rpa.actions.impl.tool;

import com.seebon.common.utils.DateUtils;
import com.seebon.common.utils.IdCardUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.IdCardTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.IdCardMethod;
import com.seebon.rpa.context.runtime.RobotConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Slf4j
@RobotAction(name = "身份证处理",
        targetType = IdCardTarget.class,
        comment = "身份证处理")
public class IdCardUtil extends AbstractAction {

    @Conditions({"birthday:format,varKey", "sex:varKey"})
    @ActionArgs(value = "操作类型", required = true, dict = IdCardMethod.class)
    private String actionType;

    @ActionArgs(value = "出生日期格式")
    private String format;

    @ActionArgs(value = "结果变量")
    private String varKey;

    @Override
    public void run() {
        String idCard = getTarget();
        try {
            switch (IdCardMethod.valueOf(actionType)) {
                case birthday: {
                    String birthByIdCard = IdCardUtils.getBirthByIdCard(idCard);
                    if (StringUtils.isNotBlank(format)) {
                        Date yyyyMMdd = DateUtils.strToDate(birthByIdCard, "yyyyMMdd");
                        birthByIdCard = DateUtils.dateToStr(yyyyMMdd, format);
                    }
                    ctx.setVariable(varKey, birthByIdCard);
                    break;
                }
                case sex: {
                    String genderByIdCard = IdCardUtils.getGenderByIdCard(idCard);
                    ctx.setVariable(varKey, genderByIdCard);
                    break;
                }
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("【Exception】", e);
            throw new RobotConfigException("身份证处理异常");
        }
    }

}
