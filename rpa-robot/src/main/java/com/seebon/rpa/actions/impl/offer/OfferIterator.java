package com.seebon.rpa.actions.impl.offer;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.EmptyRuntimeException;
import com.seebon.rpa.entity.saas.vo.DeclareTempInfoDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * @author LY
 * @date 2023/11/1 15:58
 */
@Slf4j
@ActionUtils
@RobotAction(name = "循环读取报盘数据", targetType = NoneTarget.class, comment = "单次提交数据映射指令")
public class OfferIterator extends AbstractAction {

    @Override
    public void run() {
        String offerFilePath = ctx.getVariable("offerFilePath").toString();
        DeclareTempInfoDTO declareTempInfo = ctx.getVariable("declareTempInfo");
        ExcelReader reader = ExcelUtil.getReader(new File(offerFilePath), declareTempInfo.getSheetIndex());
        List<List<Object>> readResult = reader.read(declareTempInfo.getHeardIndex() + 1);
        if (readResult.isEmpty()) {
            throw new EmptyRuntimeException(stepCode);
        }
        for (List<Object> objects : readResult) {
            System.out.println(objects);
        }
        ctx.setVariable("offerIteratorSize", readResult.size());
        ctx.setVariable("offerIteratorIndex", 0);
        ctx.setVariable("offerIteratorCode", stepCode);
        ctx.setVariable("offerParseList", readResult);
    }

}
