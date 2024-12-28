package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.AlertType;
import com.seebon.rpa.context.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-09 15:27:40
 */
@Slf4j
@RobotAction(name = "消息弹窗", targetType = NoneTarget.class)
public class Alert extends AbstractAction {

    @Conditions({
            "alert:dataKey",
            "confirm:operationType,dataKey"
    })
    @ActionArgs(value = "alter类型", required = true, dict = AlertType.class)
    private String alertType;

    @ActionArgs(value = "操作类型", required = true, dict = OperationType.class)
    private String operationType;

    @ActionArgs(value = "结果变量")
    private String dataKey;

    @Override
    public void run() {
        long timeout = 2000;
        long start = System.currentTimeMillis();
        String alertText = "";
        while ((System.currentTimeMillis() - start) < timeout) {
            try {
                WebDriver driver = ctx.getWebDriver();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                wait.until(ExpectedConditions.alertIsPresent());
                org.openqa.selenium.Alert alert = driver.switchTo().alert();
                if (alert != null) {
                    alertText = alert.getText();
                    if (AlertType.alert.equals(AlertType.valueOf(alertType))) {
                        alert.accept();
                    } else if (AlertType.confirm.equals(AlertType.valueOf(alertType))) {
                        if (OperationType.valueOf(operationType).equals(OperationType.confirm)) {
                            alert.accept();
                        } else {
                            alert.dismiss();
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                log.error("获取消息弹窗异常,堆栈异常忽略." + e.getMessage());
            }
        }
        //读取弹框内容，放到变量
        if (StringUtils.isNotBlank(dataKey)) {
            ctx.setVariable(dataKey, alertText);
        }
        log.info("弹窗内容：" + alertText);
    }
}
