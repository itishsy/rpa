package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.ApiRespOpenIntercept;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RobotAction(name = "接口响应拦截", targetType = NoneTarget.class)
public class ApiResp extends AbstractAction {

    @ActionArgs(value = "网站接口地址urls", required = true)
    private String url;

    @ActionArgs(value = "需要获取参数keys", required = true)
    private String paramKeys;

    @ActionArgs(value = "开启拦截", dict = ApiRespOpenIntercept.class, comment = "浏览器启动已配置【开启拦截】则可不选或否")
    private String openIntercept;

    @Autowired
    private DriverTarget driverTarget;

    @Override
    public void run() {
        ctx.setVariable(RobotConstant.INTERCEPTOR_URLS, url);
        ctx.setVariable(RobotConstant.INTERCEPTOR_PARAM_KEYS, paramKeys);
        if (StringUtils.isNotBlank(openIntercept) && ApiRespOpenIntercept.TRUE.equals(ApiRespOpenIntercept.valueOf(openIntercept))) {
            WebDriver driver = ctx.getWebDriver();
            driverTarget.closeDevTools(driver);
            driverTarget.setStartOperation("openIntercept");
            driverTarget.interceptor(driver);
        }
    }
}
