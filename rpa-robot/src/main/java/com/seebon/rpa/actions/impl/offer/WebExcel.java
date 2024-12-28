package com.seebon.rpa.actions.impl.offer;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.context.enums.WebExcelType;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * @author LY
 * @date 2023/11/1 20:15
 */

@Slf4j
@ActionUtils
@RobotAction(name = "Excel数据填充到页面", targetType = NoneTarget.class, comment = "单次提交数据映射指令,依赖于 OfferIterator指令")
public class WebExcel extends AbstractAction {

    @ActionArgs(value = "xpath地址", style = DynamicFieldType.text, required = true)
    private String xpath;

    @ActionArgs(value = "excel数据列", dict = WebExcelNumLine.class, required = true)
    private WebExcelNumLine webExcelNumLine;

    @ActionArgs(value = "类型", dict = WebExcelType.class, required = true)
    private WebExcelType webExcelType;

    private int iframeIndex = 0;

    @Override
    public void run() {
        List<List<Object>> offerParseList = ctx.getVariableToList("offerParseList");
        Integer rowIndex = ctx.getVariable("offerIteratorIndex");
        if (CollectionUtils.isEmpty(offerParseList) || rowIndex == null) {
            throw new RobotInterruptException("没有获取到offerIterator依赖数据或报盘解析的数据为空");
        }
        WebDriver webDriver = ctx.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(ctx.getAction().getTimeout()));
        String text = offerParseList.get(rowIndex).get(webExcelNumLine.getIndex()).toString();
        if (webExcelType == WebExcelType.input) {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(switchToFrame(xpath))));
            try {
                element.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            element.sendKeys(text);
        } else if (webExcelType == WebExcelType.select) {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(switchToFrame(xpath))));
            Select select = new Select(element);
            select.selectByVisibleText(text);
        } else {
            String liXpath = "";
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            String script = "document.documentElement.scrollTop=document.body.scrollHeight";
            js.executeScript(script);
            if (xpath.contains("*[contains(string(),")) {
                int start = xpath.lastIndexOf("*[contains(string(),");
                int end = xpath.indexOf(")]", start);
                liXpath = xpath.substring(0, start + 20) + "\"" + text + "\"" + xpath.substring(end);
                System.out.println(liXpath);
                List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(switchToFrame(liXpath))));
                for (int i = elements.size() - 1; i >= 0; i--) {
                    WebElement element = elements.get(i);
                    if (element.isEnabled() && element.isDisplayed()) {
                        String content = element.getText();
                        if (content != null && content.trim().contains(text)) {
                            try {
                                js.executeScript("arguments[0].scrollIntoView(true);", element);
                                element.click();
                                break;
                            } catch (Exception ignore) {
                                js.executeScript("arguments[0].click();", element);
                            }
                        }
                    }
                }
            } else {
                int start = xpath.indexOf("string()=\"");
                int end = xpath.indexOf("\"]", start);
                liXpath = xpath.substring(0, start + 10) + text + xpath.substring(end);
                List<WebElement> liElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(liXpath)));
                System.out.println(liXpath);
                for (WebElement element : liElement) {
                    if (element.isEnabled() && element.isDisplayed()) {
                        String content = element.getText();
                        if (content != null && content.trim().equals(text)) {
                            try {
                                js.executeScript("arguments[0].scrollIntoView(true);", element);
                                element.click();
                                break;
                            } catch (Exception ignore) {
                                js.executeScript("arguments[0].click();", element);
                            }
                        }
                    }
                }
            }
        }
        switchToDefault();
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

    private void switchToDefault() {
        WebDriver driver = ctx.getWebDriver();
        driver.switchTo().defaultContent();
    }
}
