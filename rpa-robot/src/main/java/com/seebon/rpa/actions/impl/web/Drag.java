package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.DragWay;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Random;

@Slf4j
@RobotAction(name = "拖动元素", targetType = ElementTarget.class)
public class Drag extends AbstractAction {

    @ActionArgs(value = "水平距离")
    private String xOffset;

    @ActionArgs(value = "拖动方式", comment = "不选默认为移动", dict = DragWay.class)
    private String dragWay;

    @Override
    public void run() {
        try {
            WebDriver driver = ctx.getWebDriver();
            WebElement element = getTarget();

            Actions action = new Actions(driver);
            if (StringUtils.isBlank(dragWay) || DragWay.valueOf(dragWay).equals(DragWay.move)) {
                action.clickAndHold(element);
                int[] times = splitInto(parse(xOffset, Integer.class), new int[3]);
                for (int i = 0; i < times.length; i++) {
                    action.moveByOffset(times[i], i%2==0?-2:2);
                }
                for (int i = 0; i < 11; i++) {
                    action.moveByOffset(0, i%2==0?-1:1);
                }
                action.moveToElement(element).pause(800).release();
                org.openqa.selenium.interactions.Action actions = action.build();
                actions.perform();
            } else if (DragWay.valueOf(dragWay).equals(DragWay.dragAndDrop)) {
                action.dragAndDropBy(element, Integer.parseInt(xOffset), 0).pause(800).perform();
            }
        } catch (Exception e) {
            log.error("【Exception】", e);
        }
    }

    private int[] splitInto(int number, int[] numberOfSplits) {
        Random random = new Random();
        for (int i = 0; i < numberOfSplits.length; i++) {
            if (number > 1) {
                int j = random.nextInt(number - 1) + 1;
                number -= j;
                if (i == numberOfSplits.length - 1) {
                    numberOfSplits[i] = j + number;
                } else {
                    numberOfSplits[i] = j;
                }
            } else if (number == 1) {
                numberOfSplits[i] = number;
                break;
            }
        }
        return numberOfSplits;
    }
}
