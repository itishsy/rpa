package com.seebon.rpa.actions.impl.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ExeJSType;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

@RobotAction(name = "执行JS语句", targetType = ElementTarget.class, comment = "执行JS语句")
public class ExeJS extends AbstractAction {

    @ActionArgs(value = "执行类型", dict = ExeJSType.class)
    private String type;

    @ActionArgs("执行语句")
    private String script;

    @ActionArgs("执行结果变量")
    private String dataKey;

    @Override
    public void run() {
        WebDriver driver = ctx.getWebDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        if (StringUtils.isBlank(type)) {
            if (StringUtils.isBlank(dataKey)) {
                js.executeScript(script);
            } else {
                Object value = js.executeScript(script);
                System.out.println("script value=" + value);
                if (value != null) {
                    ctx.setVariable(dataKey, value);
                }
            }
            return;
        }
        WebElement element = getTarget();
        switch (ExeJSType.valueOf(type)) {
            case removeAttr: {
                for (String attr : script.split(",")) {
                    js.executeScript("arguments[0].removeAttribute(arguments[1])", element, attr);
                }
                break;
            }
            case updateStyle: {
                String style = element.getAttribute("style");
                if (StringUtils.isNotBlank(style)) {
                    String[] split = script.split(":");
                    Map<String, String> map = Maps.newHashMap();
                    map.put(split[0].trim(), split[1].replace(";", ""));

                    Map<String, String> map1 = Maps.newHashMap();
                    String[] split1 = style.split(";");
                    for (String s : split1) {
                        if (StringUtils.isNotBlank(s)) {
                            String[] split2 = s.split(":");
                            map1.put(split2[0].trim(), split2[1].trim());
                        }
                    }

                    for(String key : map.keySet()) {
                        map1.put(key, map.get(key));
                    }
                    List<String> cssList = Lists.newArrayList();
                    for(String key : map1.keySet()) {
                        cssList.add(key.concat(":").concat(map1.get(key)));
                    }
                    System.out.println("修改后的样式：" + StringUtils.join(cssList, ";"));
                    js.executeScript("arguments[0].style.cssText=arguments[1]", element, StringUtils.join(cssList, ";"));
                } else {
                    js.executeScript("arguments[0].style.cssText=arguments[1]", element, script);
                }
                break;
            }
            case downloadFile: {
                this.cacheFilePath();
                if (StringUtils.isNotBlank(script)) {
                    js.executeScript(script);
                }
                break;
            }
            default:
                Object value = js.executeScript(script);
                System.out.println("script value=" + value);
                if (value != null) {
                    ctx.setVariable(dataKey, value);
                }
                break;
        }
    }
}
