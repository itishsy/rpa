package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.ElementUtil;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.InputMethod;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.event.KeyEvent;

@Slf4j
@RobotAction(name = "输入", targetType = ElementTarget.class)
public class Input extends AbstractAction {

    @ActionArgs(value = "输入方式", dict = InputMethod.class)
    private String inputMethod;

    @ActionArgs("输入xpath")
    private String inputXPath;

    @ActionArgs("输入内容")
    private String inputValue;

    @ActionArgs(value = "删除属性")
    private String removeAttr;

    @Autowired
    private ElementUtil elementUtil;

    @Override
    public void run() {
        WebElement element = this.getTarget();
        WebDriver driver = ctx.getWebDriver();
        if (StringUtils.isNotBlank(removeAttr)) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (String attr : removeAttr.split(",")) {
                js.executeScript("arguments[0].removeAttribute(arguments[1])", element, attr);
            }
        }
        if (StringUtils.isNotBlank(inputMethod)) {
            switch (InputMethod.valueOf(inputMethod)) {
                case clickAndInput: {
                    element.click();
                    element.clear();
                    Convert.sleep(1);
                    if (StringUtils.isNotBlank(inputXPath)) {
                        try {
                            WebElement elementEx = elementUtil.findByXpath(driver, inputXPath, 30);
                            elementEx.clear();
                            elementEx.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
                        } finally {
                            elementUtil.releaseToParent(driver);
                        }
                    } else {
                        element.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
                    }
                    break;
                }
                case clickAndInputEnter: {
                    element.click();
                    element.clear();
                    Convert.sleep(1);
                    if (StringUtils.isNotBlank(inputXPath)) {
                        try {
                            WebElement elementEx = elementUtil.findByXpath(driver, inputXPath, 30);
                            elementEx.clear();
                            elementEx.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
                        } finally {
                            elementUtil.releaseToParent(driver);
                        }
                    } else {
                        element.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
                    }
                    Convert.sleep(1);
                    try {
                        Robot robot = new Robot();
                        robot.setAutoDelay(500);
                        robot.keyPress(KeyEvent.VK_ENTER);
                        robot.keyRelease(KeyEvent.VK_ENTER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case hasValueUnInput: {
                    String value = element.getAttribute("value");
                    log.info("value:{}", value);
                    if (StringUtils.isNotBlank(value)) {
                        return;
                    }
                    element.clear();
                    element.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
                    break;
                }
                case disabledUnInput: {
                    String disabled = element.getAttribute("disabled");
                    String readonly = element.getAttribute("readonly");
                    log.info("disabled:{}", disabled);
                    log.info("readonly:{}", readonly);
                    if (StringUtils.isNotBlank(disabled) && "true".equals(disabled)) {
                        return;
                    }
                    if (StringUtils.isNotBlank(readonly) && "true".equals(readonly)) {
                        return;
                    }
                    element.clear();
                    element.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
                    break;
                }
                default:
                    break;
            }
        } else {
            element.clear();
            element.sendKeys((CharSequence) (null == inputValue ? "" : inputValue));
        }
    }
}
