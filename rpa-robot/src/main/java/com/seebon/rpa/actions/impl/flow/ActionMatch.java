package com.seebon.rpa.actions.impl.flow;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@RobotAction(name = "操作判断",
        order = 1,
        comment = "执行操作判断，操作成功执行skipTo，操作失败执行failedSkipTo")
public class ActionMatch extends AbstractAction {

    private int iframeIndex = 0;

    @ActionArgs(value = "成功页面元素", required = true, comment = "判断操作成功时的页面元素的xpath")
    private String elePath;

    @ActionArgs(value = "成功页面元素名称", comment = "不填默认为sElement")
    private String eleName;

    @ActionArgs(value = "成功条件", parsed = false, required = true, style = DynamicFieldType.text)
    private String condition;

    @ActionArgs(value = "失败页面元素", comment = "判断操作失败时的页面元素的xpath")
    private String failElePath;

    @ActionArgs(value = "失败页面元素名称", comment = "不填默认为fElement")
    private String failEleName;

    @ActionArgs(value = "失败条件", parsed = false, style = DynamicFieldType.text)
    private String failCondition;

    @Override
    public void run() {
        int timeout = this.getTimeout();
        long start = System.currentTimeMillis();

        boolean success = false;
        boolean fail = false;

        WebDriverWait wait = new WebDriverWait(ctx.getWebDriver(), Duration.ofSeconds(1));

        while ((System.currentTimeMillis() - start) < timeout) {
            String elePathCopy = new String(elePath);
            elePathCopy = this.switchToFrame(elePathCopy);
            WebElement sElement = null;
            try {
                sElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(elePathCopy)));
            } catch (Exception e) {

            }
            ctx.setVariable(StringUtils.isBlank(eleName) ? "sElement" : eleName, sElement);
            boolean s = parse(condition, Boolean.TYPE);
            if (s) {
                success = true;
                this.releaseToParent();
                break;
            }
            this.releaseToParent();
            if (StringUtils.isNotBlank(failElePath)) {
                String failElePathCopy = new String(failElePath);
                failElePathCopy = this.switchToFrame(failElePathCopy);
                WebElement fElement = null;
                try {
                    fElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(failElePathCopy)));
                } catch (Exception e) {

                }
                ctx.setVariable(StringUtils.isBlank(failEleName) ? "fElement" : failEleName, fElement);
                boolean f = parse(failCondition, Boolean.TYPE);
                if (f) {
                    fail = true;
                    this.releaseToParent();
                    break;
                }
                this.releaseToParent();
            }
        }

        if (success && StringUtils.isNotBlank(skipTo)) {
            throw new RuntimeSkipTo(skipTo);
        }
        if (fail && StringUtils.isNotBlank(failedSkipTo)) {
            throw new RuntimeSkipTo(failedSkipTo);
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
            driver.switchTo().parentFrame();
            iframeIndex--;
            switchToParent();
        }
    }

    private void releaseToParent() {
        switchToParent();
        iframeIndex = 0;
    }
}
