package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.ElementUtil;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.SelectByType;
import com.seebon.rpa.utils.Convert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-04 18:13:21
 */
@RobotAction(name = "下拉选择", targetType = ElementTarget.class)
public class Select extends AbstractAction {

    @ActionArgs(value = "选择内容", required = true)
    private String selectValue;

    @ActionArgs(value = "选择类型", required = true, dict = SelectByType.class)
    private String selectByType;

    @Autowired
    private ElementUtil elementUtil;

    @Override
    public void run() {
        WebElement element = getTarget();
        WebDriver driver = ctx.getWebDriver();
        if (SelectByType.valueOf(selectByType).equals(SelectByType.byText)) {
            new org.openqa.selenium.support.ui.Select(element).selectByVisibleText(selectValue);
        } else if (SelectByType.valueOf(selectByType).equals(SelectByType.byValue)) {
            new org.openqa.selenium.support.ui.Select(element).selectByValue(selectValue);
        } else if (SelectByType.valueOf(selectByType).equals(SelectByType.byIndex)) {
            try {
                Integer index = Integer.valueOf(selectValue);
                new org.openqa.selenium.support.ui.Select(element).selectByIndex(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (SelectByType.valueOf(selectByType).equals(SelectByType.byULi)) {
            List<WebElement> lis = element.findElements(By.tagName("li"));
            for (WebElement li : lis) {
                if (li.getText().contains(selectValue)) {
                    li.click();
                }
            }
        } else if (SelectByType.valueOf(selectByType).equals(SelectByType.byClick)) {
            element.click();
            Convert.sleep(2);//休眠2秒，等待下拉框加载
            try {
                WebElement elementEx = elementUtil.findByXpath(driver, selectValue, 30);
                elementEx.click();
            } finally {
                elementUtil.releaseToParent(driver);
            }
        }
    }
}
