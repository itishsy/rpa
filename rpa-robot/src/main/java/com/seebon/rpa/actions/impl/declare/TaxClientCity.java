package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

@Slf4j
@RobotAction(name = "社保费管理客户端省份城市", order = 51, targetType = NoneTarget.class, comment = "社保费管理客户端省份城市")
public class TaxClientCity extends AbstractAction {
    @ActionArgs(value = "结果变量", required = true,comment = "新安装返回:true,反之false")
    private String resultKey;

    @Value("${tax.client.city.mapping}")
    private String mapping;

    @Override
    public void run() {
        String city = JSON.parseObject(mapping).getString(ctx.get("addrName"));
        log.info("社保管理费客户端省份城市:{}",city);
        ctx.setVariable(resultKey, city);
    }

}



