package com.seebon.rpa.actions.impl.data;

import com.alibaba.fastjson.JSONObject;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.Keyboard;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.VariableMethod;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.service.RobotTaskSessionKeepService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
@ActionUtils
@RobotAction(name = "变量操作", targetType = TargetsTarget.class, order = 0)
public class Variable extends AbstractAction {

    @Conditions({
            "add:vars",
            "append:vars",
            "put:vars",
            "remove:varName",
            "remove:varName,resultKey",
            "httpSetVal:resultKey"
    })
    @ActionArgs(value = "操作类型", required = true, dict = VariableMethod.class)
    private String actionType;

    @ActionArgs(value = "变量", comment = "Json")
    private Map<String, Object> vars;

    @ActionArgs(value = "变量名", comment = "多个以逗号隔开")
    private String varName;

    @ActionArgs(value = "结果变量", comment = "多个以逗号隔开")
    private String resultKey;

    @Autowired
    private SyncInService syncInService;
    @Autowired
    private DriverTarget driverTarget;
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;
    @Autowired
    private Keyboard keyboard;

    @Override
    public void run() {
        switch (VariableMethod.valueOf(actionType)) {
            case add: {
                for (String key : vars.keySet()) {
                    ctx.setVariable(key, vars.get(key));
                }
                break;
            }
            case append: {
                for (String key : vars.keySet()) {
                    ctx.addVariable(key, vars.get(key));
                }
                break;
            }
            case put: {
                for (String key : vars.keySet()) {
                    if (ctx.contains(key)) {
                        Object o = ctx.getVariable(key);
                        Map map;
                        if (o instanceof Map) {
                            map = (Map) o;
                            map.putAll((Map) vars.get(key));
                        }
                    } else {
                        ctx.setVariable(key, vars.get(key));
                    }
                }
                break;
            }
            case remove: {
                if (StringUtils.isNotBlank(varName)) {
                    String[] arr = varName.split(",");
                    for (String s : arr) {
                        ctx.remove(s);
                    }
                }
                break;
            }
            case contains: {
                boolean contains = contains(varName);
                ctx.setVariable(resultKey, contains);
                break;
            }
            case httpSetVal: {
                if (StringUtils.isBlank(resultKey)) {
                    break;
                }
                String[] arr = resultKey.split(",");
                if (null == ctx.getAction().getVariables().get("response")) {
                    for (String s : arr) {
                        ctx.setVariable(s, null);
                    }
                    break;
                }
                String responseStr = ctx.getAction().getVariables().get("response").toString();
                Map<String, Object> responseMap = (Map<String, Object>) JsonUtils.jsonToBean(responseStr.toString(), Map.class);
                for (String s : arr) {
                    ctx.setVariable(s, responseMap);
                }
                break;
            }
            default:
                break;
        }
    }

    public boolean contains(String key) {
        if (syncInService.isDevEnv(null) || taskSessionKeepService.getSessionKeepPort() != null) {
            if (StringUtils.isNotBlank(key) && "driver".equals(key)) {
                String runSupport = ctx.get("runSupport");
                if ("10019001".equals(runSupport) || "10019004".equals(runSupport)) {
                    ChromeDriver driver = driverTarget.remoteWebDriver(null, runSupport);
                    if (driver == null) {
                        if (syncInService.isDevEnv(null)) {
                            Integer port = driverTarget.getPort();
                            driverTarget.removePort(port);
                        }
                        return false;
                    }
                    this.showDesktop();
                    this.maximize(driver);
                    ctx.setVariable(RobotConstant.DEFAULT_DRIVER_KEY, driver);
                    return true;
                }
            }
        }
        return ctx.contains(key);
    }

    public String get(String key) {
        return ctx.get(key);
    }

    private void maximize(WebDriver driver) {
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            log.error("浏览器最大化异常,此异常可以忽略.");
        }
        Dimension dimension = driver.manage().window().getSize();
        Point point = driver.manage().window().getPosition();
        log.info("浏览器最大化成功,窗口大小:{},窗口位置:{}", dimension, point);
        if ((dimension.getHeight() == 0 && dimension.getWidth() == 0) || (point.getX() == 10 && point.getY() == 10)) {
            try {
                driver.manage().window().setPosition(new Point(-8, -8));
            } catch (Exception e) {
                log.error("浏览器最大化异常,此异常可以忽略.");
            }
        }
    }

    private void showDesktop() {
        try {
            keyboard.setKeyIn("VK_WINDOWS+VK_D");
            keyboard.run();
        } catch (Exception e) {
            log.error("启动浏览器前，显示桌面异常." + e.getMessage(), e);
        }
    }

    public JSONObject toJsonObject(String s) {
        return JSONObject.parseObject(s);
    }

    public String toJsonString(Object o) {
        return JSONObject.toJSONString(o);
    }

}
