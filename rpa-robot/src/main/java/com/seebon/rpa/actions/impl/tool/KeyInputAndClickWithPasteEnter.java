package com.seebon.rpa.actions.impl.tool;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.HashMap;

@Slf4j
@RobotAction(name = "异步操作", targetType = ElementTarget.class, comment = "异步操作")
public class KeyInputAndClickWithPasteEnter extends AbstractAction {

    @Resource
    private ElementTarget driverInstance;

    @ActionArgs(value = "粘贴的文本")
    private String txt;

    @ActionArgs(value = "输入的指令")
    private String oder;

    @ActionArgs(value = "要执行的次数")
    private String count;

    @ActionArgs(value = "获取要判断的元素的xpath")
    private String nextXpath;

    @Override
    public void run() {
        HashMap<String, String> map = new HashMap<>();
        map.put("count", count);
        map.put("oder", oder);
        map.put("nextXpath", nextXpath);
        map.put("text", txt);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("=====================开始点击===========");
                WebElement element = getTarget();
                element.click();
                log.info("=====================结束点击===========");
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Robot robot = new Robot();
                    robot.setAutoDelay(500);
                    Integer num = Integer.valueOf(map.get("count"));
                    if (num != 0) {
                        System.out.println("=====================开始up===========");
                        for (int i = 0; i < num; i++) {
                            if ("VK_UP".equals(map.get("oder"))) {
                                robot.keyPress(KeyEvent.VK_UP);
                                robot.keyRelease(KeyEvent.VK_UP);
                                System.out.println("=====================up" + i + "===========");
                            }
                        }
                        Thread.sleep(1000);
                        log.info("=====================开始回车1===========");
                        robot.keyPress(KeyEvent.VK_ENTER);
                        robot.keyRelease(KeyEvent.VK_ENTER);
                        log.info("=====================结束回车1===========");
                    }
                    Thread.sleep(2000);
                    String s = map.get("text");
                    StringSelection stringSelection = new StringSelection(s);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                    log.info("=====================开始输入===========");
                    for (int i = 0; i < s.length(); i++) {
                        int val = Convert.getKeyEventValue(s.charAt(i));
                        robot.keyPress(val);
                        robot.keyRelease(val);
                    }
                    log.info("=====================结束输入===========");
                    log.info("=====================开始回车2===========");
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                    log.info("=====================结束回车2===========");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        log.info("=====================开始run线程1===========");
        t.start();
        log.info("=====================结束run线程1===========");
        try {
            Thread.sleep(2000);
            log.info("=====================开始run线程2===========");
            t2.start();
            log.info("=====================结束run线程2===========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
