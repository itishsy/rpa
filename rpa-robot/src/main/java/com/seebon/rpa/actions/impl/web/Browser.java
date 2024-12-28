package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.BrowserAction;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Set;

@Slf4j
@RobotAction(name = "浏览器", targetType = DriverTarget.class)
public class Browser extends AbstractAction {

    @ActionArgs(value = "操作类型", required = true, dict = BrowserAction.class)
    private String actionType;

    @Override
    public void run() {
        WebDriver driver = getTarget();
        switch (BrowserAction.valueOf(actionType)) {
            case start:
                if (driver == null) {
                    throw new RobotConfigException("无法启动浏览器");
                }
                this.focus(driver);
                break;
            case quit: {
                if (null != driver) {
                    String openInterceptApi = ctx.get(RobotConstant.OPEN_INTERCEPT_API);
                    Convert.quit(driver, openInterceptApi);
                    ctx.remove(RobotConstant.DEFAULT_DRIVER_KEY);
                    throw new RobotInterruptException(stepName);
                }
                break;
            }
            case quitAll: {
                if (null != driver) {
                    String openInterceptApi = ctx.get(RobotConstant.OPEN_INTERCEPT_API);
                    Convert.quit(driver, openInterceptApi);
                    ctx.remove(RobotConstant.DEFAULT_DRIVER_KEY);
                    throw new RobotInterruptException(stepName);
                }
                break;
            }
            case switchTo: {
                ctx.setVariable(RobotConstant.DEFAULT_DRIVER_KEY, driver);
                break;
            }
            case clearBrowserData: {
                WebDriver webDriver = ctx.getWebDriver();
                webDriver.manage().deleteAllCookies();
                break;
            }
            case closeOtherTab:{
                Set<String> handles = driver.getWindowHandles();
                String lastOpenPage = ctx.get(RobotConstant.LAST_OPEN_PAGE);
                Set<String> lastOpenPageList = ctx.get(RobotConstant.LAST_OPEN_PAGE_LIST);

                for (String handle : handles) {
                    if (!lastOpenPageList.contains(handle)) {
                        driver.switchTo().window(handle);
                        driver.close();
                    }
                }
                driver.switchTo().window(lastOpenPage);
                break;
            }
            default:
                break;
        }
    }

    private void focus(WebDriver driver) {
        try {
            //休眠1秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //鼠标点击
            org.openqa.selenium.Point point = driver.manage().window().getPosition();
            Robot robot = new Robot();
            robot.setAutoDelay(500);
            robot.mouseMove(point.x + 500, point.y + 500);
            robot.mousePress(KeyEvent.BUTTON1_MASK);
            robot.mouseRelease(KeyEvent.BUTTON1_MASK);
            System.out.println("point.x=" + point.x + ",point.y=" + point.y);
        } catch (Exception e) {
            log.error("启动浏览器，鼠标点击获取焦点异常." + e.getMessage(), e);
        }
    }
}
