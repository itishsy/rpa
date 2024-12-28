package com.seebon.rpa.actions.impl.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.ClickArgs;
import com.seebon.rpa.context.enums.ClickType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "点击", targetType = ElementTarget.class)
public class Click extends AbstractAction {

    @ActionArgs(value = "点击方式", required = true, dict = ClickType.class)
    private String clickType;

    @ActionArgs(value = "点击参数", dict = ClickArgs.class)
    private String clickArgs;

    @ActionArgs(value = "结果变量")
    private String dataKey;

    @Autowired
    private DriverTarget driverTarget;

    @Override
    public void run() {
        WebElement element = getTarget();

        //TODO 缓存文件-后面删除
        this.cacheFilePath();

        if (StringUtils.isNotBlank(clickArgs)) {
            if (!ClickArgs.notSwitchPage.equals(ClickArgs.valueOf(clickArgs))) {
                //切换窗口
                this.switchToWindowBefore();
            }
            if (ClickArgs.notNullClick.equals(ClickArgs.valueOf(clickArgs)) && (element == null || !element.isDisplayed())) {
                return;
            }
            if (ClickArgs.clear.equals(ClickArgs.valueOf(clickArgs)) && element != null) {
                element.clear();
                return;
            }
            if (ClickArgs.downloadFile.equals(ClickArgs.valueOf(clickArgs))) {
                this.cacheFilePath();
            }
        } else {
            //切换窗口
            this.switchToWindowBefore();
        }
        WebDriver driver = ctx.getWebDriver();
        switch (ClickType.valueOf(clickType)) {
            case click: {
                element.click();
                break;
            }
            case doubleClick: {
                Actions action = new Actions(driver);
                action.doubleClick(element).perform();
                break;
            }
            case mouseClick: {
                org.openqa.selenium.Point point = driver.manage().window().getPosition();
                this.mouseClick(point.x + 500, point.y + 500);
                break;
            }
            case jsClick: {
                JavascriptExecutor js = ((JavascriptExecutor) driver);
                js.executeScript("arguments[0].click();", element);
                break;
            }
            case actionClick: {
                Actions actions = new Actions(driver);
                actions.click(element).perform();
                break;
            }
            case clickAndEnter: {
                Thread clickTh = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        element.click();
                    }
                });
                Thread enterTh = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Robot robot = new Robot();
                            robot.setAutoDelay(500);
                            robot.keyPress(KeyEvent.VK_ENTER);
                            robot.keyRelease(KeyEvent.VK_ENTER);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                clickTh.start();
                try {
                    Thread.sleep(2000);
                    enterTh.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case clickAndAlert: {
                String alertText = "";
                try {
                    //点击并关闭弹窗
                    element.click();
                    //等待弹窗出现
                    long timeout = 6000;
                    long start = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - start) < timeout) {
                        try {
                            Alert alert = driver.switchTo().alert();
                            if (alert != null) {
                                alertText = alert.getText();
                                alert.accept();
                                break;
                            }
                        } catch (Exception e) {
                            log.error("点击并关闭消息异常,堆栈异常忽略." + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error("点击并关闭消息异常." + e.getMessage(), e);
                }
                if (StringUtils.isNotBlank(dataKey)) {
                    ctx.setVariable(dataKey, alertText);
                }
                log.info("弹窗内容：" + alertText);
                break;
            }
            case clickAndConfirm: {
                List<String> alertTexts = Lists.newArrayList();
                try {
                    try {
                        //点击并关闭弹窗
                        element.click();
                    } catch (Exception e) {
                        log.error("点击并确认消息,点击异常." + e.getMessage());
                    }
                    //等待弹窗出现
                    long timeout = 6000;
                    long start = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - start) < timeout) {
                        for (int i = 0; i < 5; i++) {
                            String alertText = this.acceptAlert(driver);
                            if (StringUtils.isBlank(alertText)) {
                                continue;
                            }
                            alertTexts.add(alertText);
                        }
                    }
                } catch (Exception e) {
                    log.error("点击并确认消息异常." + e.getMessage(), e);
                }
                log.info("弹窗内容：" + JSON.toJSONString(alertTexts));
                break;
            }
            case rightClick:
                break;
            case mouseClickAndScreen: {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                //log.info("屏幕分辨率-height=" + screenSize.getHeight() + ",width=" + screenSize.getWidth());
                this.mouseClick((int) screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
                break;
            }
            default:
                break;
        }
        if (StringUtils.isBlank(clickArgs) || !ClickArgs.notSwitchPage.equals(ClickArgs.valueOf(clickArgs))) {
            this.switchToWindowAfter();
        }
    }

    private void switchToWindowBefore() {
        WebDriver driver = ctx.getWebDriver();
        ctx.setVariable(RobotConstant.WINDOW_HANDLE, driver.getWindowHandles().stream().collect(Collectors.toList()));
    }

    private void switchToWindowAfter() {
        //等待窗口打开在切换
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebDriver driver = ctx.getWebDriver();
        Set<String> handles = driver.getWindowHandles();
        String current = driver.getWindowHandle();
        boolean reopenIntercept = false;
        if (StringUtils.isNotBlank(clickArgs) && ClickArgs.closeOtherHandle.equals(ClickArgs.valueOf(clickArgs))) {
            for (String handle : handles) {
                if (handle.equals(current)) {
                    driver.close();
                    reopenIntercept = true;
                }
            }
        }
        for (String handle : handles) {
            if (!ctx.contains(RobotConstant.WINDOW_HANDLE, handle)) {
                driver.switchTo().window(handle);
                if (reopenIntercept) {
                    driverTarget.closeDevTools(driver);
                    driverTarget.setStartOperation("openIntercept");
                    driverTarget.interceptor(driver);
                }
                ctx.addVariable(RobotConstant.WINDOW_HANDLE, handle);
            }
        }
    }

    private void mouseClick(Integer x, Integer y) {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(500);
            robot.mouseMove(x, y);
            robot.mousePress(KeyEvent.BUTTON1_MASK);
            robot.mouseRelease(KeyEvent.BUTTON1_MASK);
            log.info("鼠标点击 width=" + x + ",height=" + y);
        } catch (Exception e) {
            log.error("鼠标点击异常." + e.getMessage(), e);
        }
    }

    private String acceptAlert(WebDriver driver) {
        try {
            Alert alert = driver.switchTo().alert();
            if (alert != null) {
                alert.accept();
                Thread.sleep(1000);
                log.info("确认弹窗成功");
                return alert.getText();
            }
        } catch (Exception e) {
            log.error("点击并确认消息异常,堆栈异常忽略." + e.getMessage());
        }
        return null;
    }
}
