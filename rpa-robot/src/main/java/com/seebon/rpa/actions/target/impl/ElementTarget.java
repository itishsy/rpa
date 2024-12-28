package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.enums.AttributeType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
@ActionTarget
public class ElementTarget extends AbstractTarget<WebElement> {

    @Setter
    @ActionArgs(value = "属性", dict = AttributeType.class)
    private String attrType;

    @Setter
    @ActionArgs(value = "属性值")
    private String attrValue;

    private int iframeIndex = 0;

    @Override
    public WebElement fetchTarget() {
        if (StringUtils.isBlank(attrType) || StringUtils.isBlank(attrValue)) {
            return null;
        }
        int actTimeout = ctx.getAction().getTimeout();
        int fetchTimeout = actTimeout > 1 ? (actTimeout - 1) : actTimeout;
        return getElement(AttributeType.valueOf(attrType), attrValue, fetchTimeout);
    }

    public WebElement getElement(AttributeType attrType, String attrValue, long timeout) {
        try {
            attrValue = this.switchToFrame(attrValue);
            WebDriverWait wait = new WebDriverWait(ctx.getWebDriver(), Duration.ofSeconds(timeout));
            switch (attrType) {
                case id:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(attrValue)));
                case _name:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.name(attrValue)));
                case xpath:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(attrValue)));
                case className:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.className(attrValue)));
                case tagName:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(attrValue)));
                case cssSelector:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(attrValue)));
                case linkText:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(attrValue)));
                default:
                    return null;
            }
        } catch (Exception e) {
            log.warn("未找到WebElement对象" + e.getMessage());
            return null;
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

    private void switchToParent() {
        if (iframeIndex > 0) {
            WebDriver driver = ctx.getWebDriver();
            try {
                driver.switchTo().parentFrame();
            } finally {
                iframeIndex--;
                switchToParent();
            }
        }
    }

    @Override
    public void release() {
        switchToParent();
        iframeIndex = 0;
    }
}
