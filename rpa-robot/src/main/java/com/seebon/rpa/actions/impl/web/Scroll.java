package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ScrollType;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RobotAction(name = "滚动", targetType = ElementTarget.class)
public class Scroll extends AbstractAction {

    @ActionArgs(value = "滚动类型", required = true, dict = ScrollType.class)
    private String scrollType;

    @Override
    public void run() {
        WebElement element = getTarget();
        WebDriver webDriver = ctx.getWebDriver();
        switch (ScrollType.valueOf(scrollType)) {
            case top: {
                if (element == null) {
                    ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(document.body.scrollHeight,0)");
                } else {
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
                }

                break;
            }
            case bottom: {
                if (element == null) {
                    ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0,document.body.scrollHeight)");
                } else {
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollTo(0, 100000000);", element);
                }

                break;
            }
            default:
                break;
        }
    }
}
