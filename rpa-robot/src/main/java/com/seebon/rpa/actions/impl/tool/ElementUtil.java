package com.seebon.rpa.actions.impl.tool;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class ElementUtil {

    private int iframeIndex = 0;

    public String switchToFrame(WebDriver driver, String attrValue) {
        if (!attrValue.startsWith("[iframe")) {
            return attrValue;
        }
        for (int i = 0; i < 100; i++) {
            String iframeName = "[iframe" + i + "]";
            if (attrValue.startsWith(iframeName)) {
                driver.switchTo().frame(i);
                iframeIndex++;
                attrValue = attrValue.substring(iframeName.length());
                if (attrValue.startsWith("[iframe")) {
                    return switchToFrame(driver, attrValue);
                }
                break;
            }
        }
        return attrValue;
    }

    public void switchToParent(WebDriver driver) {
        if (iframeIndex > 0) {
            driver.switchTo().parentFrame();
            iframeIndex--;
            switchToParent(driver);
        }
    }

    public void releaseToParent(WebDriver driver) {
        switchToParent(driver);
        iframeIndex = 0;
    }

    public WebElement findByXpath(WebDriver driver, String xpath, Integer timeOut) {
        if (timeOut == null) {
            timeOut = 1;
        }
        WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = null;
        try {
            String xpath1 = this.switchToFrame(driver, xpath);
            element = driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath1)));
        } catch (Exception e) {
            log.error("根据xpath获取element异常：" + e.getMessage(), e);
        }
        return element;
    }
}
