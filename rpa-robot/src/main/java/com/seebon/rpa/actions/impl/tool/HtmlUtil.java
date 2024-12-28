package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.io.FileUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.HtmlTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.utils.file.TableToExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Author created By charles
 * @Description:
 * @Date 2023/2/16 14:12
 * @Modifide By:
 */
@Slf4j
@RobotAction(name = "html操作", targetType = HtmlTarget.class, comment = "html操作")
public class HtmlUtil extends AbstractAction {

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        try {
            File src = getTarget();
            List<String> list = Lists.newArrayList();
            FileUtil.readUtf8Lines(src, list);
            List<Map<String, Object>> maps = TableToExcelUtil.parseListFormTable(list.get(0));
            ctx.setVariable(dataKey, maps);
        } catch (Exception e) {
            log.error("【Exception】", e);
            throw new RobotConfigException("解析html操作异常");
        }
    }
}
