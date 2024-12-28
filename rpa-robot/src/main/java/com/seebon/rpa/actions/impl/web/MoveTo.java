package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.RobotAction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-10 10:49:56
 */
@RobotAction(name = "移动到", targetType = ElementTarget.class)
public class MoveTo extends AbstractAction {

    @Override
    public void run() {
        WebElement element = getTarget();
        WebDriver driver = ctx.getWebDriver();
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }
}
