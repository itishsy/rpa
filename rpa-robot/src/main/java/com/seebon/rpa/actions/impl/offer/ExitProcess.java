package com.seebon.rpa.actions.impl.offer;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ExitProcessEnums;
import com.seebon.rpa.context.runtime.ExitException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/12/8  17:16
 */
@Slf4j
@ActionUtils
@RobotAction(name = "出错退出", targetType = NoneTarget.class, comment = "出错退出")
public class ExitProcess extends AbstractAction {

    @ActionArgs(value = "xpath地址", style = DynamicFieldType.text, required = true)
    private String xpath;

    @ActionArgs(value = "xpath文本", style = DynamicFieldType.text, required = true)
    private String xpathText;

    @ActionArgs(value = "条件", dict = ExitProcessEnums.class)
    private ExitProcessEnums singleDropList;

    @ActionArgs(value = "错误展示信息", style = DynamicFieldType.text)
    private String errorMsg;

    private int iframeIndex = 0;

    @Override
    public void run() {
        WebDriver webDriver = ctx.getWebDriver();
        if (singleDropList == null) {
            singleDropList = ExitProcessEnums.CONTAINS;
        }
        if (singleDropList == ExitProcessEnums.CONTAINS) {
            String s = switchToFrame(xpath);
            WebElement element = webDriver.findElement(By.xpath(s));
            if (element.getText().contains(xpathText)) {
                if (StringUtils.isNotEmpty(errorMsg)) {
                    throw new ExitException(errorMsg);
                }
                throw new ExitException("中止所有流程");
            }
        }
        if (singleDropList == ExitProcessEnums.EQUALS) {
            String s = switchToFrame(xpath);
            WebElement element = webDriver.findElement(By.xpath(s));
            if (element.getText().equals(xpathText)) {
                if (StringUtils.isNotEmpty(errorMsg)) {
                    throw new ExitException(errorMsg);
                }
                throw new ExitException("中止所有流程");
            }
        }

    }

    private String switchToFrame(String attrValue) {
        if (attrValue.startsWith("[iframe")) {
            for (int i = 0; i < 100; i++) {
                String iframeName = "[iframe" + i + "]";
                if (attrValue.startsWith(iframeName)) {
                    WebDriver driver = ctx.getWebDriver();
                    driver.switchTo().frame(i);
                    iframeIndex++;
                    attrValue = attrValue.substring(iframeName.length());
                    if (attrValue.startsWith("[iframe")) {
                        return switchToFrame(attrValue);
                    }
                    break;
                }
            }
        }
        return attrValue;
    }
}
