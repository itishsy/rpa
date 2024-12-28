package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.BrowserType;
import com.seebon.rpa.context.enums.PageAction;
import com.seebon.rpa.context.enums.PageOperationType;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RobotAction(name = "网页", targetType = NoneTarget.class)
public class Page extends AbstractAction {

    @Conditions({"access:actionArgs,operationType,type",
            "accessAndDo:actionArgs,pw,type",
            "open:actionArgs,operationType,type",
            "close:actionArgs,type",
            "back:actionArgs,type",
            "switchTo:actionArgs,type",
            "refresh:actionArgs,type",
            "windowNumber:dataKey,type",
            "session:actionArgs,type",
            "currentUrl:dataKey,type",
    })
    @ActionArgs(value = "操作类型", required = true, dict = PageAction.class)
    private String actionType;

    @ActionArgs(value = "参数")
    private String actionArgs;

    @ActionArgs(value = "密码")
    private String pw;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @ActionArgs(value = "浏览器", dict = BrowserType.class, comment = "当前浏览器")
    private String type;

    @ActionArgs(value = "操作类型", dict = PageOperationType.class)
    private String operationType;

    @Autowired
    private DriverTarget driverTarget;

    @Override
    public void run() {
        WebDriver driver = ctx.getDriver();
        if (driver == null) {
            if (StringUtils.isNotBlank(type)) {
                driverTarget.setType(type);
                driver = driverTarget.fetchTarget();
            } else {
                throw new RobotConfigException("请先打开浏览器");
            }
        }
        if (StringUtils.isNotBlank(operationType)) {
            if (PageOperationType.downloadFile.equals(PageOperationType.valueOf(operationType))) {
                this.cacheFilePath();
            }
        }
        final Set<String> handles = driver.getWindowHandles();
        switch (PageAction.valueOf(actionType)) {
            case open: {
                ((JavascriptExecutor) driver).executeScript("window.open(\"" + actionArgs + "\");");
                boolean isOnly = driver.getWindowHandles().size() == 1;
                for (String handle : driver.getWindowHandles()) {
                    if (isOnly || !handle.equals(driver.getWindowHandle())) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }
                break;
            }
            case access: {
                ((JavascriptExecutor) driver).executeScript("window.focus();");
                driver.manage().timeouts().pageLoadTimeout(getTimeout(), TimeUnit.SECONDS);//元素等待时间（隐式等待）
                this.maximize(driver);
                driver.get(actionArgs);
                String current = driver.getWindowHandle();
                ctx.setVariable(RobotConstant.LAST_OPEN_PAGE, current);
                ctx.setVariable(RobotConstant.LAST_OPEN_PAGE_LIST, driver.getWindowHandles());
                break;
            }
            case accessAndDo: {
                ((JavascriptExecutor) driver).executeScript("window.focus();");
                driver.manage().timeouts().pageLoadTimeout(getTimeout(), TimeUnit.SECONDS);//元素等待时间（隐式等待）
                this.maximize(driver);
                this.enter();
                driver.get(actionArgs);
                break;
            }
            case back: {
                driver.navigate().back();
                break;
            }
            case forward: {
                driver.navigate().forward();
                break;
            }
            case refresh: {
                driver.navigate().refresh();
                break;
            }
            case switchTo: {
                boolean isOnly = handles.size() == 1;
                for (String handle : handles) {
                    if (isOnly || !handle.equals(driver.getWindowHandle())) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }
                break;
            }
            case close: {
                if (handles.size() > 1) {
                    final String current = driver.getWindowHandle();
                    driver.close();
                    for (String handle : handles) {
                        if (!handle.equals(current)) {
                            driver.switchTo().window(handle);
                            break;
                        }
                    }
                } else {
                    driver.close();
                    ctx.remove(RobotConstant.DEFAULT_DRIVER_KEY);
                }
                break;
            }
            case windowNumber: {
                ctx.setVariable(dataKey, handles.size());
                break;
            }
            case currentUrl: {
                ctx.setVariable(dataKey,ctx.getWebDriver().getCurrentUrl());
                break;
            }
            case closeOtherHandle: {
                String current = driver.getWindowHandle();
                driver.switchTo().window(current);
                driver.close();
                for (String handle : handles) {
                    if (!handle.equals(current)) {
                        driver.switchTo().window(handle);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private void enter() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Robot robot = new Robot();
                robot.setAutoDelay(500);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);

                if (StringUtils.isNotBlank(pw)) {
                    Thread.sleep(3000);
                    robot.setAutoDelay(500);
                    if ("VK_TAB".equals(pw)) {
                        robot.keyPress(KeyEvent.VK_TAB);
                        robot.keyRelease(KeyEvent.VK_TAB);
                    } else {
                        boolean isUpper = false;
                        for (int i = 0; i < pw.length(); i++) {
                            int val = Convert.getKeyEventValue(String.valueOf(pw.charAt(i)).toUpperCase().charAt(0));
                            if (Character.isUpperCase(pw.charAt(i))) {
                                robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                                robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
                                isUpper = true;
                            }
                            log.info("val:{}", val);
                            robot.keyPress(val);
                            robot.keyRelease(val);
                            if (isUpper) {
                                robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                                robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
                                isUpper = false;
                            }
                        }
                    }
                    robot.setAutoDelay(500);
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("robot执行完成");
            }
        }).start();
    }

    private void maximize(WebDriver driver) {
        try {
            driver.manage().window().maximize();//浏览器最大化
        } catch (Exception e) {
            log.error("浏览器最大化异常,此异常可以忽略.");//不打印异常堆栈
        }
    }
}
