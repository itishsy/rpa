package com.seebon.rpa.actions.impl.tool;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.Convert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

@Slf4j
@RobotAction(name = "键盘操作", targetType = NoneTarget.class, comment = "键盘操作")
public class Keyboard extends AbstractAction {

    @Setter
    @ActionArgs(value = "键入内容")
    private String keyIn;

    @Setter
    @ActionArgs(value = "输入参数")
    private String keyArgs;

    @Override
    public void run() {
        try {
            Robot robot = new Robot();
            if (StringUtils.isNotBlank(keyIn)) {
                String[] words = keyIn.split(",");
                for (String word : words) {
                    robot.setAutoDelay(500);
                    if (!word.contains("+")) {
                        int w = KeyEvent.class.getField(word).getInt(null);
                        robot.keyPress(w);
                        robot.keyRelease(w);
                    } else {
                        String[] cob = word.split("\\+");
                        if (cob.length == 2) {
                            int c0 = KeyEvent.class.getField(cob[0]).getInt(null);
                            int c1 = KeyEvent.class.getField(cob[1]).getInt(null);

                            if (c0 == KeyEvent.VK_CONTROL && c1 == KeyEvent.VK_V) {
                                StringSelection stringSelection = new StringSelection(keyArgs);
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                            }

                            robot.keyPress(c0);
                            robot.keyPress(c1);
                            robot.keyRelease(c1);
                            robot.keyRelease(c0);
                        } else if (cob.length == 3) {
                            int c0 = KeyEvent.class.getField(cob[0]).getInt(null);
                            int c1 = KeyEvent.class.getField(cob[1]).getInt(null);
                            int c2 = KeyEvent.class.getField(cob[2]).getInt(null);

                            robot.keyPress(c0);
                            robot.keyPress(c1);
                            robot.keyPress(c2);
                            robot.keyRelease(c2);
                            robot.keyRelease(c1);
                            robot.keyRelease(c0);
                        }
                    }
                }
            } else {
                boolean isUpper = false;
                for (int i = 0; i < keyArgs.length(); i++) {
                    int val = Convert.getKeyEventValue(String.valueOf(keyArgs.charAt(i)).toUpperCase().charAt(0));
                    if (Character.isUpperCase(keyArgs.charAt(i))) {
                        robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                        robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
                        isUpper = true;
                    }
                    robot.keyPress(val);
                    robot.keyRelease(val);
                    if (isUpper) {
                        robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                        robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
                        isUpper = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("键盘操作完成");
        }
    }
}
